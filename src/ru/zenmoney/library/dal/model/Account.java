package ru.zenmoney.library.dal.model;

import android.database.Cursor;
import ru.zenmoney.library.dal.trigger.AccountTrigger;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: chedim
 * Date: 16.03.13
 * Time: 21:33
 * To change this template use File | Settings | File Templates.
 */
public class Account extends AbstractModel {

    public Account(Cursor from) {
        super(from);
    }

    public Account() {
        super();
    }

    public Account(Long id) throws Exception {
        this();
        loadByKey(id);
    }

    @Override

    public void init() {
        setTrigger(new AccountTrigger());
    }

    @Override
    public String getSQLTable() {
        return "account";
    }

    public BigDecimal getBalance() {
        return getAsBigDecimal("balance");
    }

    public void setBalance(BigDecimal balance) {
        put("balance", balance);
    }

    public void changeBalance(BigDecimal from, BigDecimal to) {
        BigDecimal balance = getBalance();
        balance.subtract(from);
        balance.add(to);
        setBalance(balance);
    }

    public static void changeBalance(Long id, BigDecimal from, BigDecimal to, boolean writeChanged) throws Exception {
        Account acc = new Account();
        acc.loadByKey(id);
        acc.changeBalance(from, to);
        acc.save(writeChanged);
    }
}
