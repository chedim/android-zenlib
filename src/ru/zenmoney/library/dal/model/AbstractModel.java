package ru.zenmoney.library.dal.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import ru.zenmoney.library.dal.commons.Links;
import ru.zenmoney.library.dal.commons.TableLink;
import ru.zenmoney.library.dal.trigger.AbstractTrigger;
import ru.zenmoney.library.dal.DatabaseHelper;
import ru.zenmoney.library.dal.RowSet;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.security.KeyException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: chedim
 * Date: 16.03.13
 * Time: 16:59
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractModel {
    protected ContentValues source, changes;
    protected AbstractTrigger trigger;
    protected String tableName, keyField;
    protected HashMap<String, Class<? extends AbstractModel>> links;

    public AbstractModel() {
        init();
        this.tableName = getTableName();
        this.keyField = getKeyField();
        source = new ContentValues();
        changes = new ContentValues();
    }

    public AbstractModel(Cursor from) {
        this();
        DatabaseUtils.cursorRowToContentValues(from, source);
        changes = new ContentValues(source);
    }

    public AbstractModel(HashMap<String, Object> from) {
        this();
        Map.Entry<String, Object> field;
        Object value;
        for (Iterator it = from.entrySet().iterator(); it.hasNext(); ) {
            field = (Map.Entry<String, Object>) it.next();
            value = field.getValue();
            if (value != null) {
                put(field.getKey(), field.getValue().toString());
            } else {
                changes.putNull(field.getKey());
            }
        }
    }

    public AbstractModel(String tableName) {
        this();
        this.tableName = tableName;
    }

    public AbstractModel(String tableName, Cursor from) {
        this(from);
        this.tableName = tableName;
    }

    public AbstractModel(String tableName, String keyField) {
        this();
        this.tableName = tableName;
        this.keyField = keyField;
    }

    public AbstractModel(String tableName, String keyField, Cursor from) {
        this(tableName, keyField);
        DatabaseUtils.cursorRowToContentValues(from, source);
        changes = new ContentValues(source);
    }

    public AbstractModel(Long key) throws Exception {
        this();
        loadByKey(key);
    }

    public static AbstractModel instantiate(ParameterizedType type) throws IllegalAccessException, InstantiationException {
        Class<? extends AbstractModel> c = (Class<? extends AbstractModel>) (((ParameterizedType) RowSet.class.getGenericSuperclass()).getActualTypeArguments()[0]);
        return c.newInstance();
    }

    public static Long getUnixTimestamp() {
        return (new Date()).getTime() / 1000;
    }

    public void replaceFieldWithServerId(String field, Class<? extends AbstractModel> linkedClass) throws Exception {
        Long id = getAsLong(field);
        if (id == null) return;
        AbstractModel linkedObject = linkedClass.getDeclaredConstructor().newInstance();
        linkedObject.loadByKey(id);
        Long newId = linkedObject.getServerKey();
        if (newId != null && newId > 0) {
            put(field, newId);
        }
    }

    public void replaceFieldWithClientId(String field, TableLink link) throws Exception {
        Long id = getAsLong(field);
        if (id == null) return;
        AbstractModel linkedObject = link.target.getDeclaredConstructor().newInstance();
        linkedObject.loadByServerKey(id);
        String newId = linkedObject.getKey();
        put(field, newId);
    }

    public boolean toSync() throws Exception {
        HashMap<String, TableLink> links = getLinks();
        if (links != null) {
            for (Iterator it = links.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, TableLink> entry = (Map.Entry<String, TableLink>) it.next();
                TableLink link = entry.getValue();
                try {
                    replaceFieldWithServerId(entry.getKey(), link.target);
                } catch (NotFoundException e) {
                    if (link.hard) {
                        if (link.skip) {
                            return false;
                        } else {
                            throw e;
                        }
                    } else {
                        putNull(entry.getKey());
                    }
                }
            }
        }
        if (trigger != null) {
            if (!trigger.onExport(this)) {
                return false;
            }
        }
        return true;
    }

    public ContentValues getChanges() {
        return changes;
    }

    public void setChanges(ContentValues changes) {
        this.changes = changes;
    }

    public boolean fromSync() throws Exception {
        HashMap<String, TableLink> links = getLinks();
        if (links != null) {
            for (Iterator it = links.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, TableLink> entry = (Map.Entry<String, TableLink>) it.next();
                TableLink link = entry.getValue();
                try {
                    replaceFieldWithClientId(entry.getKey(), link);
                } catch (NotFoundException e) {
                    if (link.hard) {
                        if (link.skip) {
                            return false;
                        } else {
                            throw e;
                        }
                    } else {
                        putNull(entry.getKey());
                    }
                }
            }
        }
        if (trigger != null) {
            if (!trigger.onImport(this)) {
                return false;
            }
        }
        return true;
    }

    private void putNull(String key) {
        changes.putNull(key);
    }

    protected HashMap<String, TableLink> getLinks() {
        HashMap<String, TableLink> ret = null;
        try {
            Field f = Links.class.getField(tableName);
            ret = (HashMap<String, TableLink>) f.get(null);
            if (ret == null) throw new Exception();
        } catch (Exception e) {
            ret = new HashMap<String, TableLink>();
        }
        return ret;
    }

    public abstract void init();

    public abstract String getSQLTable();

    public String getTableName() {
        return getSQLTable();
    }

    public void delete() throws Exception {
        delete(true);
    }

    public void delete(boolean writeChanged) throws Exception {
        SQLiteDatabase db = DatabaseHelper.getWritableConnection();
        String[] args = {getKey()};
        String where = getKeyField() + " = ?";
        db.beginTransaction();
        try {
            if (trigger != null) trigger.onBeforeDelete(source, writeChanged);
            try {
                db.delete('`' + tableName + '`', where, args);
                if (writeChanged) {
                    if (getServerKey() != null) {
                        ContentValues deletion = new ContentValues();
                        deletion.put("object", tableName);
                        deletion.put("object_id", getServerKey());
                        deletion.put("stamp", getUnixTimestamp());
                        db.insertOrThrow("deletion", null, deletion);
                    }
                }
                db.setTransactionSuccessful();
                if (trigger != null) trigger.onAfterDelete(source);
            } catch (Exception e) {
                if (trigger != null) trigger.onDeleteFail(source, e);
                throw e;
            }
        } finally {
            db.endTransaction();
            DatabaseHelper.free();
        }
    }

    public void save() throws Exception {
        save(true);
    }

    public void save(boolean writeChanged) throws Exception {
        SQLiteDatabase db = DatabaseHelper.getWritableConnection();
        String _id = getKey();
//        if (changes.getAsString("changed") != null) {
        Long changed = getUnixTimestamp();
        if (writeChanged) {
            changes.put("changed", changed);
        }
//        }
        db.beginTransaction();
        try {
            if (_id == null) {
                if (trigger != null) trigger.onBeforeInsert(changes, writeChanged);
                ContentValues saveObject = escapeColumns(changes);
                try {
                    _id = String.valueOf(db.insertOrThrow('`' + tableName + '`', null, saveObject));
                } catch (Exception e) {
                    if (trigger != null) trigger.onInsertFail(changes, e);
                    throw e;
                }
                loadByKey(_id);
                if (trigger != null) trigger.onAfterInsert(source);
            } else {
                if (trigger != null) trigger.onBeforeUpdate(source, changes, writeChanged);
                ContentValues saveObject = escapeColumns(changes);
                try {
                    String[] args = {getKey()};
                    db.update('`' + tableName + '`', changes, keyField + " = ?", args);
                } catch (Exception e) {
                    if (trigger != null) trigger.onUpdateFail(source, changes, e);
                    throw e;
                }
                source = changes;
                if (trigger != null) trigger.onAfterUpdate(source, changes);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("db", e.getMessage(), e);
            throw e;
        } finally {
            db.endTransaction();
            DatabaseHelper.free();
        }
    }

    public void loadByKey(Long id) throws Exception {
        loadByKey(String.valueOf(id));
    }

    public void loadByServerKey(Long id) throws Exception {
        SQLiteDatabase db = DatabaseHelper.getWritableConnection();
        String where = "id = ";
        String[] args = {id.toString()};
        Cursor rowset = db.query('`' + tableName + '`', null, where + "?", args, null, null, null, null);
        if (!rowset.moveToFirst()) {
            throw new NotFoundException(tableName + " with " + where + id.toString() + " not found");
        }
        source = new ContentValues();
        DatabaseUtils.cursorRowToContentValues(rowset, source);
        changes = new ContentValues(source);
    }

    public void loadByKey(String id) throws Exception {
        SQLiteDatabase db = DatabaseHelper.getWritableConnection();
        String where = keyField + " = ";
        String[] args = {id};
        Cursor rowset = db.query('`' + tableName + '`', null, where + "?", new String[]{id}, null, null, null, null);
        if (!rowset.moveToFirst()) {
            throw new NotFoundException("Row with " + where + id + " not found");
        }
        source = new ContentValues();
        DatabaseUtils.cursorRowToContentValues(rowset, source);
        changes = new ContentValues(source);
    }

    public String getKeyField() {
        return "_id";
    }

    public String getKey() {
        return changes.getAsString(getKeyField());
    }

    public ContentValues escapeColumns(ContentValues cv) {
        ContentValues result = new ContentValues();
        Set<Map.Entry<String, Object>> set = cv.valueSet();
        Iterator<Map.Entry<String, Object>> it = set.iterator();
        Map.Entry<String, Object> entry;
        String key;
        String val;
        do {
            entry = it.next();
            key = entry.getKey();
            if (key.charAt(0) != '`') {
                key = "`" + key + "`";
            }
            val = String.valueOf(entry.getValue());
            result.put(key, val);
        } while (it.hasNext());
        return result;
    }

    public ArrayList<String> describeColumns() {
        ArrayList<String> ret = new ArrayList<String>();
        Set<Map.Entry<String, Object>> fields = changes.valueSet();
        for (Iterator<Map.Entry<String, Object>> it = fields.iterator(); it.hasNext(); ) {
            ret.add(it.next().getKey());
        }
        return ret;
    }

    public RowSet getChangeSet(Long localRevision) throws Exception {
        SQLiteDatabase db = DatabaseHelper.getReadableConnection();
        String where = "changed > ?";
        String[] args = {String.valueOf(localRevision)};
        RowSet result = new RowSet(getClass(), db.query('`' + tableName + '`', null, where, args, null, null, keyField));
        return result;
    }

    public AbstractTrigger getTrigger() {
        return trigger;
    }

    protected void setTrigger(AbstractTrigger trigger) {
        this.trigger = trigger;
    }

    // SETTERS
    public void put(String key, String value) {
        changes.put(key, value);
    }

    public void put(String key, Long value) {
        changes.put(key, value);
    }

    public void put(String key, BigDecimal value) {
        changes.put(key, value.toString());
    }

    // GETTERS
    public String getAsString(String key) {
        return changes.getAsString(key);
    }

    public Long getAsLong(String key) {
        return changes.getAsLong(key);
    }

    public BigDecimal getAsBigDecimal(String key) {
        return new BigDecimal(changes.getAsString(key));
    }

    public Long getServerKey() {
        return getAsLong("id");
    }

    public class NotFoundException extends Exception {
        public NotFoundException(String s) {
            super(s);
        }
    }

}
