package ru.zenmoney.library.dal.model;

import android.database.Cursor;

/**
 * Created with IntelliJ IDEA.
 * User: chedim
 * Date: 16.03.13
 * Time: 18:40
 * To change this template use File | Settings | File Templates.
 */
public class User extends AbstractModel {

    public User(Cursor from) {
        super(from);
    }

    public User() {
        super();
    }

    @Override
    public void init() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getSQLTable() {
        return "user";
    }
}
