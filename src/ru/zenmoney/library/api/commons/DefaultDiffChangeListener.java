package ru.zenmoney.library.api.commons;

import android.content.Context;
import android.util.Log;
import ru.chedim.Utils;
import ru.zenmoney.library.api.OAuthSettings;
import ru.zenmoney.library.api.v2.Diff;
import ru.zenmoney.library.dal.model.AbstractModel;

/**
 * Created with IntelliJ IDEA.
 * User: chedim
 * Date: 17.03.13
 * Time: 3:30
 * To change this template use File | Settings | File Templates.
 */
public class DefaultDiffChangeListener implements Diff.ChangeListener {
    protected Context context;
    public DefaultDiffChangeListener (Context context) {
        this.context = context;
    }

    @Override
    public void processChange(String section, Long serverId, Long clientId, Long timestamp, AbstractModel values) throws Exception {
        Log.w("sync", "Received: "+section+"#("+serverId+", "+String.valueOf(clientId)+")");
        values.save(false);
    }

    @Override
    public void processDeletion(AbstractModel deleted, Long timestamp) throws Exception {
        deleted.delete(false);
    }

    @Override
    public void finish(Revision newRevision) throws Exception {
        OAuthSettings oas = new OAuthSettings(context);
        oas.setRevision(newRevision);
        Utils.alert(context, "Sync", "Synchronization OK :)");
    }
}
