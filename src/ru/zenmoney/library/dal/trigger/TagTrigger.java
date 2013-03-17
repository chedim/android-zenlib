package ru.zenmoney.library.dal.trigger;

import android.content.ContentValues;
import ru.zenmoney.library.dal.RowSet;
import ru.zenmoney.library.dal.model.TagGroup;

/**
 * Created with IntelliJ IDEA.
 * User: chedim
 * Date: 16.03.13
 * Time: 22:55
 * To change this template use File | Settings | File Templates.
 */
public class TagTrigger extends AbstractTrigger {
    @Override
    public void onBeforeDelete(ContentValues row, boolean writeChanged) throws Exception {
        RowSet tg = new RowSet(TagGroup.class);
        String id = row.getAsString("id");
        String[] args = {id, id};
        tg.addQueryResult("tag0 = ? OR tag1 = ?", args);
        tg.delete(writeChanged);
    }
}
