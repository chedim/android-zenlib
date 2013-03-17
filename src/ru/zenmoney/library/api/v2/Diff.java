package ru.zenmoney.library.api.v2;

import com.fasterxml.jackson.core.*;
import org.pojava.datetime.DateTime;
import org.scribe.builder.api.Api;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import ru.zenmoney.library.api.AuthActivity;
import ru.zenmoney.library.api.OAuthProtocol;
import ru.zenmoney.library.api.commons.Revision;
import ru.zenmoney.library.dal.model.AbstractModel;
import ru.zenmoney.library.dal.model.Deletion;
import ru.zenmoney.library.dal.RowSet;

import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: chedim
 * Date: 12.03.13
 * Time: 16:21
 * To change this template use File | Settings | File Templates.
 */
public class Diff extends OAuthProtocol {

    private ChangeListener changeListener;
    private Long syncStartLocalTime;
    private Class<? extends AbstractModel> openedSection;
    private HashMap<String, Class<? extends AbstractModel>> finishedSections = new HashMap<String, Class<? extends AbstractModel>>();

    public Diff(Class<? extends Api> provider, String key, String secret) {
        super(provider, key, secret);
    }

    public void send(Token token, Revision revision, Class<? extends AbstractModel>[] structure) throws Exception {
        String endpoint = "http://api."+AuthActivity.ZENSERVER+"/v2/diff/"+revision.remote+"/";
        connect(Verb.POST, endpoint);
        generator.writeStartObject();
        Map.Entry<String, Class<?>> pair;
        String table;
        Class<? extends AbstractModel> accessor;
        AbstractModel accessorObject;
        RowSet diff;
        syncStartLocalTime = AbstractModel.getUnixTimestamp();
        for (int i = 0; i < structure.length; i++) {
            accessor = structure[i];
            try {
                accessorObject = accessor.getConstructor().newInstance();
                diff = accessorObject.getChangeSet(revision.local);
                if (diff != null)
                    put(accessor, diff);
            } catch (Exception e) {
                throw e;
            }
        }

        putDeletions((new Deletion()).getChangeSet(revision.local));

        generator.writeStringField("diff_timestamp", revision.remote.toString());
        generator.writeStringField("client_timestamp", AbstractModel.getUnixTimestamp().toString());
        generator.writeStringField("api_version", "2");
        generator.writeStringField("app_name", "ru.chedim.zenmoney");
        generator.writeStringField("app_version", "0.1");
        generator.writeEndObject();

        generator.close();

        send(token);
        parseResponse();
    }

    private void parseResponse() throws Exception {
        Long newRevision = null;
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.getCurrentName();
            if (fieldName == null) {
                String err = parser.getValueAsString();
                if (err != null && err.length() > 0) {
                    throw new Exception(parser.getValueAsString());
                }
            }
            if (fieldName != null && fieldName.equals("diff_timestamp")) {
                newRevision = parser.getValueAsLong();
                continue;
            }
            switch (parser.nextToken()) {
                case START_ARRAY:
                    // here starts changed object
                    while (parser.nextToken() != JsonToken.END_ARRAY) {
                        processChange(fieldName, parser);
                    }
                    break;
                case START_OBJECT:
                    if (fieldName.equals("user")) {
                        processChange(fieldName, parser);
                    } else {
                        throw new errInvalidResponseFormat();
                    }
                    break;
            }
        }

        changeListener.finish(new Revision(syncStartLocalTime, newRevision));
    }

    protected void processChange(String section, JsonParser parser) throws Exception {
        Class <? extends AbstractModel> c = finishedSections.get(section);
        if (c == null) return;
        AbstractModel change = c.newInstance();
        AbstractModel source = c.newInstance();
        Long serverId = null, clientId = null, timestamp = null;
        String delTable = "";
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String key = parser.getCurrentName();
            parser.nextToken();
            String val = parser.getValueAsString();
            if (section.equals("deletion")) {
                if (key.equals("object")) {
                    delTable = val;
                } else if (key.equals("object_id")) {
                    serverId = Long.valueOf(val);
                    try {
                        source.loadByServerKey(serverId);
                    } catch (AbstractModel.NotFoundException e) {}
                } else if (key.equals("stamp")) {
                    timestamp = parseDate(val);
                }
            } else {
                if (key.equals("server_id") || key.equals("id")) {
                    serverId = Long.valueOf(val);
                    key = "id";
                    try {
                        source.loadByServerKey(serverId);
                    } catch (AbstractModel.NotFoundException e) {}
                } else if (key.equals("client_id")) {
                    try {
                        clientId = Long.valueOf(val);
                        try {
                            source.loadByKey(clientId);
                        } catch (AbstractModel.NotFoundException e) {}
                    } catch (NumberFormatException e) {
                        clientId = null;
                    }
                    key = "_id";
                } else if (key.equals("changed")) {
                    timestamp = parseDate(val);
                }
                change.put(key, val);
            }
        }
        if (section.equals("deletion")) {
            try {
                source.loadByServerKey(serverId);
            } catch (AbstractModel.NotFoundException e) {
                return;
            }
            changeListener.processDeletion(source, timestamp);
        } else {
            if (source != null) {
                source.setChanges(change.getChanges());
                if (!source.fromSync()) return;
                change = source;
            }
            changeListener.processChange(section, serverId, clientId, timestamp, change);
        }
    }

    private Long parseDate(String date) throws Exception {
        try {
            Long result = DateTime.parse(date).getSeconds() * 1000;
            return result;
        } catch (Exception x) {
            try {
                Long result = Long.valueOf(date);
                return result;
            } catch (Exception e) {
                throw e;
            }
        }
    }


    public void putInfo(String name, String value) throws IOException, InstantiationException, IllegalAccessException {
        endSection();
        generator.writeStringField(name, value);
    }

    private void putDeletions(RowSet deletions) throws IOException, InstantiationException, IllegalAccessException {
        if (openedSection != null) {
            endSection();
        }
        String key;
        generator.writeArrayFieldStart("deletion");
        if (deletions != null) {
            for (int i = 0; i < deletions.size(); i++) {
                Deletion row = (Deletion) deletions.get(i);
                generator.writeStartObject();
                for (Iterator<String> it = row.describeColumns().iterator(); it.hasNext(); ) {
                    key = it.next();
                    generator.writeStringField(key, row.getAsString(key));
                }
                generator.writeEndObject();
            }
        }
        generator.writeEndArray();

    }

    private void put(Class<? extends AbstractModel> section, RowSet items) throws Exception {
        if (openedSection == null) {
            startSection(section);
        }
        if (section != openedSection) {
            endSection();
            startSection(section);
        }
        if (items.size() < 1) return;
        String key;
        for (int i = 0; i < items.size(); i++) {
            AbstractModel row = (AbstractModel) items.get(i);
            if (!row.toSync()) return;
            if (row == null) continue;
            generator.writeStartObject();
            for (Iterator<String> it = row.describeColumns().iterator(); it.hasNext(); ) {
                key = it.next();
                if (key == null) continue;
                String value = row.getAsString(key);
                if (key.equals("_id")) key = "client_id";
                if (key.equals("id")) key = "server_id";
                if(value == null || value.equals("null")) {
                    generator.writeNullField(key);
                } else {
                    generator.writeStringField(key, value);
                }
            }
            generator.writeEndObject();
        }
    }

    private void startSection(Class<? extends AbstractModel> section) throws errDataSended, IllegalAccessException, InstantiationException, IOException, errSectionClosed {
        if (parser != null) {
            throw new errDataSended();
        }
        AbstractModel am = section.newInstance();
        if (finishedSections.containsKey(am.getTableName())) {
            throw new errSectionClosed(section);
        }

        if (openedSection != null) {
            endSection();
        }

        openedSection = section;
        AbstractModel accessor = section.newInstance();

        generator.writeArrayFieldStart(accessor.getTableName());
    }

    private void endSection() throws IOException, IllegalAccessException, InstantiationException {
        if (openedSection != null) {
            AbstractModel am = openedSection.newInstance();
            generator.writeEndArray();
            finishedSections.put(am.getTableName(), openedSection);
            openedSection = null;
        }
    }


    public void setChangeListener(ChangeListener cl) {
        changeListener = cl;
    }

    public interface ChangeListener {
        public void processChange(String section, Long serverId, Long clientId, Long timestamp, AbstractModel values) throws Exception;

        public void processDeletion(AbstractModel deleted, Long timestamp) throws Exception;

        public void finish(Revision newRevision) throws Exception;
    }

    public class errSectionClosed extends Exception {
        protected Class<? extends AbstractModel> section;

        public errSectionClosed(Class<? extends AbstractModel> section) {
            super("Section " + section.getName() + " is closed.");
            this.section = section;
        }
    }

    public class errDataSended extends Exception {
    }

    public class errNoSectionOpened extends Exception {
    }

    public class errInvalidSection extends Exception {
    }

    public class errChangeListenerNotSet extends Exception {
    }

    public class errInvalidResponseFormat extends Exception {
    }

}
