package ru.zenmoney.library.api;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;
import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import ru.zenmoney.library.api.commons.DefaultDiffChangeListener;
import ru.zenmoney.library.api.commons.Revision;
import ru.zenmoney.library.api.v1.Instrument;
import ru.zenmoney.library.api.v2.Diff;
import ru.zenmoney.library.dal.model.AbstractModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chedim
 * Date: 17.03.13
 * Time: 2:38
 * To change this template use File | Settings | File Templates.
 */
public class ZenmoneyApi extends DefaultApi10a {
    public static final String ZENSERVER = "zenmoney.ru";
    private static final String AUTHORIZE_URL = "http://api."+ZENSERVER+"/access/?mobile&oauth_token=%s&ooauth_callback=";
    private static final String REQUEST_TOKEN_ENDPOINT = "http://api."+ZENSERVER+"/oauth/request_token";
    private static final String ACCESS_TOKEN_ENDPOINT = "http://api."+ZENSERVER+"/oauth/access_token";


    @Override
    public String getRequestTokenEndpoint() {
        return REQUEST_TOKEN_ENDPOINT;
    }

    @Override
    public String getAccessTokenEndpoint() {
        return ACCESS_TOKEN_ENDPOINT;
    }

    @Override
    public String getAuthorizationUrl(Token requestToken) {
        return String.format(AUTHORIZE_URL, requestToken.getToken());
    }

    protected static String APP_KEY, APP_SECRET;
    protected static Context context;
    protected static OAuthSettings settings;

    public static void setApplicationToken(Context context, String key, String secret) {
        ZenmoneyApi.context = context;
        APP_KEY = key;
        APP_SECRET = secret;
        settings = new OAuthSettings(context);
    }

    public static void sync(Class<? extends AbstractModel>[] structure) throws Exception {
        Token token = getToken();
        Diff diff = new Diff(ZenmoneyApi.class, APP_KEY, APP_SECRET);
        diff.setChangeListener(new DefaultDiffChangeListener(context));
        Revision revision = settings.getRevision();
        if (revision.remote == 0l) {
            loadInstruments();
        }
        diff.send(token, settings.getRevision(), structure);
    }

    private static void loadInstruments() throws Exception {
        Token token = getToken();
        Instrument protocol = new Instrument(ZenmoneyApi.class, APP_KEY, APP_SECRET);
        protocol.send(token, new OAuthProtocol.ObjectListener() {
            @Override
            public void onObject(HashMap<String, Object> object) throws Exception {
                ru.zenmoney.library.dal.model.Instrument instrument = new ru.zenmoney.library.dal.model.Instrument(object);
                instrument.save();
            }

            @Override
            public void onComplete() throws Exception {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
    }

    public static boolean isAuthorized() {
        return (settings.getToken(APP_KEY, APP_SECRET) != null);
    }

    public static Token getToken() throws Exception {
        Token token = settings.getToken(APP_KEY, APP_SECRET);
        if (token == null) throw new Exception("Unauthorized");
        return token;
    }
}
