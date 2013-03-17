package ru.zenmoney.library.dal.model;

import android.database.Cursor;
import ru.zenmoney.library.dal.commons.TableLink;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: chedim
 * Date: 16.03.13
 * Time: 22:54
 * To change this template use File | Settings | File Templates.
 */
public class Tag extends AbstractModel {

    public Tag() {
        super();
    }

    public Tag(Cursor from) {
        super(from);
    }

    @Override
    protected HashMap<String,TableLink> getLinks() {
        return null;
    }

    @Override
    public void init() {

    }

    @Override
    public String getSQLTable() {
        return "tag";
    }
}
