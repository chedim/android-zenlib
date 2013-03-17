package ru.zenmoney.library.api;

import android.content.Context;
import org.scribe.model.Token;
import ru.zenmoney.library.AbstractSettings;
import ru.zenmoney.library.api.commons.Revision;

/**
 * Created with IntelliJ IDEA.
 * User: chedim
 * Date: 16.03.13
 * Time: 23:19
 * To change this template use File | Settings | File Templates.
 */
public class OAuthSettings extends AbstractSettings {

    public OAuthSettings(Context context) {
        super(context);
    }

    @Override
    public String getSettingsName() {
        return getClass().getName();
    }

    public void setToken(Token consumer) {
        put("key", consumer.getToken());
        put("secret", consumer.getSecret());
    }

    public Token getToken(String appKey, String appSecret) {
        String key = getString("key"), secret = getString("secret");
        if (key == null)
            return null;
        Token ret = new Token(key, secret);
        return ret;
    }

    public void setRevision(Revision revision) {
        put("local_revision", revision.local);
        put("remote_revision", revision.local);
    }

    public Revision getRevision() {
        return new Revision(settings.getLong("local_revision", 0l), settings.getLong("remote_revision", 0l));
    }
}
