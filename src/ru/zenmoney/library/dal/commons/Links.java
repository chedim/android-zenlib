package ru.zenmoney.library.dal.commons;

import ru.zenmoney.library.dal.model.*;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: chedim
 * Date: 17.03.13
 * Time: 6:03
 * To change this template use File | Settings | File Templates.
 */
public class Links {
    public final static HashMap<String, TableLink> transaction = new HashMap<String, TableLink>();
//    public final static HashMap<String, TableLink> account = new HashMap<String, TableLink>();
    public final static HashMap<String, TableLink> transaction_tag = new HashMap<String, TableLink>();
    public final static HashMap<String, TableLink> tag_group = new HashMap<String, TableLink>();
    public final static HashMap<String, TableLink> connection_data = new HashMap<String, TableLink>();
    static {
        transaction.put("account_income", new TableLink(Account.class, true, true));
        transaction.put("account_outcome", new TableLink(Account.class, true, true));
//        account.put("connection", Connection.class);
        transaction_tag.put("transaction", new TableLink(Transaction.class, true, true));
        transaction_tag.put("tag_group", new TableLink(TagGroup.class, true, true));
        transaction_tag.put("tag0", new TableLink(Tag.class, true, true));
        transaction_tag.put("tag1", new TableLink(Tag.class, true, true));
        tag_group.put("tag0", new TableLink(Tag.class, true, true));
        tag_group.put("tag1", new TableLink(Tag.class, true, true));
        connection_data.put("connection", new TableLink(Connection.class, true, true));
        connection_data.put("account", new TableLink(Account.class, true, true));
    };
}
