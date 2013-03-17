package ru.zenmoney.library.dal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created with IntelliJ IDEA.
 * User: chedim
 * Date: 16.03.13
 * Time: 16:34
 * To change this template use File | Settings | File Templates.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    protected final static String DB_NAME = "database";
    protected static SQLiteDatabase db;
    protected static int conns;
    protected static Context context;
    protected static DatabaseHelper instance;
    protected static String[] structure = DbInit.sql;

    protected void setStructure(String[] sql) {
        structure = sql;
    }

    public DatabaseHelper (Context context) {
        super(context, DB_NAME, null, structure.length);
        this.context = context;
        instance = this;
    }

    public static SQLiteDatabase getWritableConnection() {
        return instance.getWritableDatabase();
    }

    public static SQLiteDatabase getReadableConnection() {
        return instance.getReadableDatabase();
    }

    public static void free() {
        instance.close();
    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase() {
        conns++;
        if (db == null || db.isReadOnly()) {
            db = super.getWritableDatabase();
        }
        return db;
    }

    @Override
    public synchronized SQLiteDatabase getReadableDatabase() {
        conns++;
        if (db == null) {
            db = super.getReadableDatabase();
        }
        return db;
    }

    @Override
    public synchronized void close() {
        if(--conns == 0) {
            super.close();
            db = null;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        int queryCount = structure.length;
        db.beginTransaction();
        for (int i = 0; i<queryCount; i++) {
            db.execSQL(structure[i]);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.setVersion(queryCount);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int i = oldVersion; i < newVersion; i++) {
            db.execSQL(structure[i]);
        }
    }

    public static void beginTransaction() {
        if (db == null) {
            getWritableConnection();
        }
        db.beginTransaction();
    }

    public static void setTransactionSuccesfull() {
        db.setTransactionSuccessful();
    }

    public static void endTransaction() {
        db.endTransaction();
    }
}
