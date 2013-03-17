package ru.zenmoney.library.api;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.SignatureType;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import ru.zenmoney.library.LogTool;

import java.net.URI;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chedim
 * Date: 16.03.13
 * Time: 20:09
 * To change this template use File | Settings | File Templates.
 */
public abstract class AuthActivity extends Activity {

    public final static String APP_KEY = "app_key", APP_SECRET = "app_secret", DEBUG = "debug";
    public static final String ZENSERVER = "zenmoney.ru";
    private static final String AUTHORIZE_URL = "http://api."+ZENSERVER+"/access/?mobile&oauth_token=%s";
    private static final String REQUEST_TOKEN_ENDPOINT = "http://api."+ZENSERVER+"/oauth/request_token";
    private static final String ACCESS_TOKEN_ENDPOINT = "http://api."+ZENSERVER+"/oauth/access_token";
    private static final String CALLBACK_URL = "zenlib://callback";
    private static OAuthService service;

    private Token requestToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String APP_KEY = getIntent().getStringExtra(this.APP_KEY);
        String APP_SECRET = getIntent().getStringExtra(this.APP_SECRET);
        Boolean debug = getIntent().getBooleanExtra(this.DEBUG, false);
        service = new ServiceBuilder()
                .provider(ZenmoneyApi.class).apiKey(APP_KEY).apiSecret(APP_SECRET)
                .signatureType(SignatureType.Header).debug()
                .debugStream(new LogTool("ZM", debug)).build();

        String url = null;
        try {
            requestToken = service.getRequestToken();
            url = getAuthUrl(requestToken);
            WebView web = new WebView(this);
            web.getSettings().setSavePassword(false);
            web.getSettings().setJavaScriptEnabled(true);
            CookieSyncManager.createInstance(this);
            CookieSyncManager.getInstance().startSync();
            setContentView(web);
            web.getLayoutParams().width = WebView.LayoutParams.MATCH_PARENT;
            web.getLayoutParams().height = WebView.LayoutParams.MATCH_PARENT;
            web.setWebViewClient(webClient);
            web.loadUrl(url);
        } catch(Exception e) {
            onError(requestToken, url, e);
        }
    }

    private String getAuthUrl(Token requestToken) {
        String requestTokenSecret = requestToken.getSecret();
        return (service.getAuthorizationUrl(requestToken)
                .concat("&callback_url=").concat(CALLBACK_URL));

    }

    protected abstract void onComplete(Token consumer);
    protected abstract void onError(Token requestToken, String authUrl, Throwable error);
    protected abstract void onRequestDeclined();

    protected WebViewClient webClient = new WebViewClient() {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            try {
                if (url.matches(CALLBACK_URL+".*")) {
                    List<NameValuePair> params = URLEncodedUtils.parse(new URI(url), "UTF-8");
                    final String token = params.get(0).toString().substring(12);
                    final String verifier = params.get(1).toString().substring(15);

                    Token temp = new Token(token, requestToken.getSecret());
                    Token accessToken = service.getAccessToken(requestToken, new Verifier(verifier));

                    onComplete(accessToken);
                    view.loadUrl("about:blank");
                } else {
                    view.loadUrl(url);
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    };
}
