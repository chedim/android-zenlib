package ru.zenmoney.library.dal.trigger;

import android.content.ContentValues;
import ru.zenmoney.library.dal.RowSet;
import ru.zenmoney.library.dal.model.ConnectionData;

/**
 * Created with IntelliJ IDEA.
 * User: chedim
 * Date: 17.03.13
 * Time: 0:53
 * To change this template use File | Settings | File Templates.
 */
public class ConnectionTrigger extends AbstractTrigger {
    @Override
    public void onBeforeDelete(ContentValues row, boolean writeChanged) throws Exception {
        RowSet datas = new RowSet(ConnectionData.class);
        String id = row.getAsString("_id");
        datas.addQueryResult("connection = ?", new String[]{id});
        datas.delete(writeChanged);
    }

}
