package ru.zenmoney.library.api.v1;

import org.scribe.builder.api.Api;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import ru.zenmoney.library.api.OAuthProtocol;
import ru.zenmoney.library.api.ZenmoneyApi;
import ru.zenmoney.library.dal.RowSet;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: chedim
 * Date: 17.03.13
 * Time: 4:16
 * To change this template use File | Settings | File Templates.
 */
public class Instrument extends OAuthProtocol {
    protected final String url = "http://api."+ZenmoneyApi.ZENSERVER+"/v1/instrument/currency/";
    public Instrument(Class<? extends Api> provider, String key, String secret) throws IOException {
        super(provider, key, secret);
        connect(Verb.GET, url);
    }

    public void send(Token token, ObjectListener listener) throws Exception {
        Response ret = send(token);
        RowSet dels = new RowSet(ru.zenmoney.library.dal.model.Instrument.class);
        dels.addQueryResult(null, null);
        dels.delete(false);
        parseResponse(listener);
    }
}
