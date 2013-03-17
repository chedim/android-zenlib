package ru.zenmoney.library.dal.model;

import android.database.Cursor;
import ru.zenmoney.library.dal.trigger.ConnectionTrigger;

/**
 * Created with IntelliJ IDEA.
 * User: chedim
 * Date: 17.03.13
 * Time: 0:52
 * To change this template use File | Settings | File Templates.
 */
public class Connection extends AbstractModel {

    public Connection(Cursor from) {
        super(from);
    }

    public Connection() {
        super();
    }

    public Connection(Long key) throws Exception {
        super();
        loadByKey(key);
    }

    @Override
    public void init() {
        setTrigger(new ConnectionTrigger());
    }

    @Override
    public String getSQLTable() {
        return "connection";
    }
}
