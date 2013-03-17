package ru.zenmoney.library.dal.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import ru.zenmoney.library.dal.DatabaseHelper;
import ru.zenmoney.library.dal.RowSet;

import java.lang.reflect.InvocationTargetException;

/**
 * Created with IntelliJ IDEA.
 * User: chedim
 * Date: 16.03.13
 * Time: 21:05
 * To change this template use File | Settings | File Templates.
 */
public class Deletion extends AbstractModel {

    public Deletion(Cursor from) {
        super(from);
    }

    public Deletion() {
        super();
    }

    @Override
    public void init() {

    }

    @Override
    public RowSet getChangeSet(Long localRevision) throws Exception {
        SQLiteDatabase db = DatabaseHelper.getReadableConnection();
        String where = "stamp > ?";
        String[] args = {String.valueOf(localRevision)};
        RowSet result = new RowSet(getClass(), db.query('`'+tableName+'`', null, where, args, null, null, "stamp DESC"));
        return result;
    }

    @Override
    public String getSQLTable() {
        return "deletion";
    }
}
