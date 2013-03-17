package ru.zenmoney.library.dal.trigger;

import android.content.ContentValues;
import ru.zenmoney.library.dal.RowSet;
import ru.zenmoney.library.dal.model.TransactionTag;

/**
 * Created with IntelliJ IDEA.
 * User: chedim
 * Date: 16.03.13
 * Time: 22:51
 * To change this template use File | Settings | File Templates.
 */
public class TagGroupTrigger extends AbstractTrigger {
    @Override
    public void onBeforeDelete(ContentValues row, boolean writeChanged) throws Exception {
        RowSet tt = new RowSet(TransactionTag.class);
        tt.addQueryResult("tag_group = ?", new String[]{row.getAsString("_id")});
        tt.delete(writeChanged);
    }

}
