package ru.zenmoney.library.dal;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import org.pojava.lang.Processor;
import ru.zenmoney.library.dal.model.AbstractModel;
import ru.zenmoney.library.dal.model.Account;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: chedim
 * Date: 16.03.13
 * Time: 17:51
 * To change this template use File | Settings | File Templates.
 */
public class RowSet extends ArrayList {
    protected Class<? extends AbstractModel> c;

    public RowSet(Class<? extends AbstractModel> c, Cursor rows) throws Exception {
        this(c);
        addFromCursor(rows);
    }

    public RowSet(Class<? extends AbstractModel> c) {
        this.c = c;
    }

    public void addQueryResult(String[] columns, String where, String[] args, String groupBy,
                               String having, String orderBy, String limit, boolean distinct) throws Exception {
        SQLiteDatabase db = DatabaseHelper.getReadableConnection();
        AbstractModel t = (AbstractModel) c.newInstance();
        Cursor rows = db.query(distinct, t.getTableName(), null, where, args, groupBy, having, orderBy, limit);
        addFromCursor(rows);
    }

    public void addQueryResult(String where, String[] args, String orderBy, String limit) throws Exception {
        addQueryResult(null, where, args, null, null, orderBy, limit, false);
    }

    public void addQueryResult(String where, String[] args) throws Exception {
        addQueryResult(where, args, null, null);
    }

    public void addFromCursor(Cursor rows) throws Exception {
        if (rows.moveToFirst()) {
            while(rows.moveToNext()) {
                try {
                    add(c.getDeclaredConstructor(Cursor.class).newInstance(rows));
                } catch (NoSuchMethodException e) {
                    throw new Exception("Class <"+c.getName()+"> has no constructor from Cursor", e);
                }
            }
        }
    }

    public void save() throws Exception {
        AbstractModel row = null;
        DatabaseHelper.beginTransaction();
        try {
            for (Iterator it = iterator(); it.hasNext(); ) {
                row = (AbstractModel) it.next();
                row.save();
            }
            DatabaseHelper.setTransactionSuccesfull();
        } finally {
            DatabaseHelper.endTransaction();
        }
    }

    public void delete() throws Exception {
        delete(true);
    }

    public void delete(boolean writeChanged) throws Exception {
        AbstractModel row = null;
        DatabaseHelper.beginTransaction();
        try {
            for (Iterator it = iterator(); it.hasNext(); ) {
                row = (AbstractModel) it.next();
                row.delete(writeChanged);
            }
            DatabaseHelper.setTransactionSuccesfull();
        } finally {
            DatabaseHelper.endTransaction();
        }
    }
}
