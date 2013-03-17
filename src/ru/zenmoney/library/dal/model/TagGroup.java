package ru.zenmoney.library.dal.model;

import android.database.Cursor;

/**
 * Created with IntelliJ IDEA.
 * User: chedim
 * Date: 16.03.13
 * Time: 22:45
 * To change this template use File | Settings | File Templates.
 */
public class TagGroup extends AbstractModel {

    public TagGroup(Cursor from) {
        super(from);
    }

    public TagGroup() {
        super();
    }

    public TagGroup(Long id) throws Exception {
        this();
        loadByKey(id);
    }

    @Override
    public void init() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getSQLTable() {
        return "tag_group";
    }
}
