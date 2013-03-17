package ru.zenmoney.library.dal.model;

import android.database.Cursor;
import ru.zenmoney.library.dal.trigger.TransactionTagTrigger;

/**
 * Created with IntelliJ IDEA.
 * User: chedim
 * Date: 16.03.13
 * Time: 22:05
 * To change this template use File | Settings | File Templates.
 */
public class TransactionTag extends AbstractModel {

    public TransactionTag(Cursor from) {
        super(from);
    }

    public TransactionTag() {
        super();
    }

    @Override
    public void init() {
        setTrigger(new TransactionTagTrigger());
    }

    @Override
    public String getSQLTable() {
        return "transaction_tag";
    }
}
