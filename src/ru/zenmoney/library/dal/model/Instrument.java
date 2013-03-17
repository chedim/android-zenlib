package ru.zenmoney.library.dal.model;

import android.database.Cursor;
import ru.zenmoney.library.dal.trigger.InstrumentTrigger;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: chedim
 * Date: 17.03.13
 * Time: 4:30
 * To change this template use File | Settings | File Templates.
 */
public class Instrument extends AbstractModel {
    public Instrument(Cursor from) {
        super(from);
        setTrigger(new InstrumentTrigger());
    }

    public Instrument() {
        super();
        setTrigger(new InstrumentTrigger());
    }
    public Instrument(HashMap<String, Object> object) {
        super(object);
        setTrigger(new InstrumentTrigger());
    }

    @Override
    public String getKeyField() {
        return "id";
    }

    @Override
    public void init() {

    }

    @Override
    public String getSQLTable() {
        return "instrument";
    }
}
