package ru.zenmoney.library.dal.model;

import android.database.Cursor;
import ru.zenmoney.library.dal.trigger.TransactionTrigger;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: chedim
 * Date: 16.03.13
 * Time: 21:45
 * To change this template use File | Settings | File Templates.
 */
public class Transaction extends AbstractModel {
    public Transaction(Cursor from) {
        super(from);
    }

    public Transaction() {
        super();
    }

    public Transaction(Long id) throws Exception {
        this();
        loadByKey(id);
    }

    @Override
    public void init() {
        setTrigger(new TransactionTrigger());
    }

    @Override
    public String getSQLTable() {
        return "transaction";
    }

    public void setSum(BigDecimal sum) throws Exception {
        int dest = sum.compareTo(BigDecimal.ZERO);
        if (dest < 0) {
            put("income", BigDecimal.ZERO);
            put("outcome", sum.abs());
        } else if (dest == 0) {
            throw new Exception("Invalid sum");
        } else {
            put("income", sum.abs());
            put("outcome", BigDecimal.ZERO);
        }
    }

    public void setTransfer(BigDecimal out, BigDecimal in) throws Exception {
        if (out.compareTo(BigDecimal.ZERO) == 0 || in.compareTo(BigDecimal.ZERO) == 0) {
            throw new Exception("invalid transfer");
        }
        put("outcome", in);
        put("income", in);
    }

    public void convertToIncome() {

    }
}
