package ru.zenmoney.library.api;

import android.util.JsonWriter;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Api;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class OAuthProtocol {

    protected OAuthService service;
    protected ServiceBuilder serviceBuilder = new ServiceBuilder();
    protected OAuthRequest request;
    protected Response response;
    protected JsonFactory  factory = new JsonFactory();
    protected JsonGenerator generator;
    protected JsonParser parser;

    public OAuthProtocol(Class<? extends Api> provider, String key, String secret) {
        service = serviceBuilder.provider(provider).apiKey(key).apiSecret(secret)
                .signatureType(SignatureType.Header).build();
    }

    public void connect(Verb verb, String url) throws IOException {
        request = new OAuthRequest(verb, url);
        generator = factory.createGenerator(getOutputStream(), JsonEncoding.UTF8);
    }

    public OutputStream getOutputStream() {
        return new OutputStream() {
            ArrayList<Integer> payload = new ArrayList<Integer>();

            @Override
            public void write(int oneByte) throws IOException {
                payload.add(oneByte);
            }

            @Override
            public void flush() throws IOException {
                byte[] bytePayload = new byte[payload.size()];
                int i = 0;
                while (payload.size() > 0) {
                    bytePayload[i++] = (byte) (int) payload.remove(0);
                }
                request.addPayload(bytePayload);
            }

            @Override
            public void close() throws IOException {
                flush();
                super.close();
            }
        };
    }

    public InputStream getInputStream() {
        return response.getStream();
    }

    public Response send(Token token) throws IOException {
        service.signRequest(token, request);
        response = request.send();
        parser = factory.createParser(getInputStream());
        return response;
    }

    public void parseResponse(ObjectListener listener) throws Exception {
        HashMap<String, Object> object = null;
        parser.nextToken();
        while(parser.hasCurrentToken()) {
            switch (parser.getCurrentToken()) {
                case START_OBJECT:
                    object = new HashMap<String, Object>();
                    break;
                case FIELD_NAME:
                    parser.nextToken();
                    object.put(parser.getCurrentName(), parser.getValueAsString());
                    break;
                case END_OBJECT:
                    listener.onObject(object);
                    break;
            }
            parser.nextToken();
        }
        listener.onComplete();
    }

    public interface  ObjectListener {
        public void onObject(HashMap<String, Object> object) throws Exception;
        public void onComplete() throws Exception;
    }
}
