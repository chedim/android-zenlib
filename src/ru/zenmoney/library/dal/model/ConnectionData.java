package ru.zenmoney.library.dal.model;

import android.database.Cursor;

/**
 * Created with IntelliJ IDEA.
 * User: chedim
 * Date: 17.03.13
 * Time: 0:02
 * To change this template use File | Settings | File Templates.
 */
public class ConnectionData extends AbstractModel {
    public ConnectionData(Cursor from) {
        super(from);
    }

    public ConnectionData() {
        super();
    }

    @Override
    public void init() {

    }

    @Override
    public String getSQLTable() {
        return "connection_data";
    }
}
