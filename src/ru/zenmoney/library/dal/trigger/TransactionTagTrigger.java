package ru.zenmoney.library.dal.trigger;

import android.content.ContentValues;
import ru.zenmoney.library.dal.model.TagGroup;

/**
 * Created with IntelliJ IDEA.
 * User: chedim
 * Date: 16.03.13
 * Time: 22:43
 * To change this template use File | Settings | File Templates.
 */
public class TransactionTagTrigger extends AbstractTrigger {
    @Override
    public void onBeforeInsert(ContentValues row, boolean writeChanged) throws Exception {
        cache(row);
    }

    @Override
    public void onBeforeUpdate(ContentValues oldRow, ContentValues newRow, boolean writeChanged) throws Exception {
        if (oldRow.getAsLong("tag_group") != newRow.getAsLong("tag_group")) {
            cache(newRow);
        }
    }

    protected void cache(ContentValues row) throws Exception {
        TagGroup tg = new TagGroup();
        tg.loadByKey(row.getAsLong("tag_group"));
        row.put("tag0", tg.getAsString("tag0"));
        row.put("tag1", tg.getAsString("tag1"));
    }

}
