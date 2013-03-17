package ru.zenmoney.library.dal.trigger;

import android.content.ContentValues;

/**
 * Created with IntelliJ IDEA.
 * User: chedim
 * Date: 17.03.13
 * Time: 4:49
 * To change this template use File | Settings | File Templates.
 */
public class InstrumentTrigger extends AbstractTrigger {
    @Override
    public void onBeforeInsert(ContentValues row, boolean writeChanged) throws Exception {
        clear(row);
    }

    @Override
    public void onBeforeUpdate(ContentValues oldRow, ContentValues newRow, boolean writeChanged) throws Exception {
        clear(newRow);
    }

    public void clear(ContentValues row) throws Exception {
        row.remove("changed");
        row.remove("value");
        row.remove("multiplier");
        row.remove("sponsored");
        row.remove("converts");
        row.remove("static_id");
        row.remove("url");
        row.remove("issuer");
        row.remove("type");
    }
}
