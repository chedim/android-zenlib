package ru.zenmoney.library.dal.trigger;

import android.content.ContentValues;
import ru.zenmoney.library.dal.RowSet;
import ru.zenmoney.library.dal.model.Account;
import ru.zenmoney.library.dal.model.TransactionTag;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: chedim
 * Date: 16.03.13
 * Time: 22:03
 * To change this template use File | Settings | File Templates.
 */
public class TransactionTrigger extends AbstractTrigger {
    @Override
    public void onBeforeDelete(ContentValues row, boolean writeChanged) throws Exception {
        Account acc = new Account();
        acc.loadByKey(row.getAsLong("account_income"));
        BigDecimal sum = new BigDecimal(row.getAsString("income"));
        Account.changeBalance(row.getAsLong("account_income"), BigDecimal.ZERO, sum, writeChanged);

        sum = new BigDecimal(row.getAsString("outcome"));
        Account.changeBalance(row.getAsLong("account_outcome"), BigDecimal.ZERO, sum, writeChanged);

        RowSet tags = new RowSet(TransactionTag.class);
        tags.addQueryResult("transaction = ?", new String[]{row.getAsString("_id")});
        tags.delete(writeChanged);
    }

    @Override
    public void onBeforeUpdate(ContentValues oldRow, ContentValues newRow, boolean writeChanged) throws Exception {
        Account acc = new Account();
        Long aio = oldRow.getAsLong("account_income"), ain = newRow.getAsLong("account_income"),
                aoo = oldRow.getAsLong("account_outcome"), aon = newRow.getAsLong("account_outcome");

        BigDecimal oincome = new BigDecimal(oldRow.getAsString("income")),
                nincome = new BigDecimal(newRow.getAsString("income")),
                ooutcome = new BigDecimal(oldRow.getAsString("outcome")),
                noutcome = new BigDecimal(newRow.getAsString("outcome"));

        if (aio == ain && aoo == aon && ain == aon) {
            Account.changeBalance(ain, oincome.subtract(ooutcome), nincome.subtract(nincome), writeChanged);
            return;
        }
        if (aio == ain) {
            if (!oincome.equals(nincome)) {
                Account.changeBalance(aio, oincome, nincome, writeChanged);
            }
        } else {
            Account.changeBalance(aio, oincome, BigDecimal.ZERO, writeChanged);
            Account.changeBalance(ain, BigDecimal.ZERO, nincome, writeChanged);
        }

        if (aoo == aon) {
            if (!ooutcome.equals(noutcome)) {
                Account.changeBalance(aoo, ooutcome.negate(), noutcome.negate(), writeChanged);
            }
        } else {
            Account.changeBalance(aoo, ooutcome.negate(), BigDecimal.ZERO, writeChanged);
            Account.changeBalance(aon, BigDecimal.ZERO, noutcome.negate(), writeChanged);
        }
    }

    @Override
    public void onBeforeInsert(ContentValues row, boolean writeChanged) throws Exception {
        Long ai = row.getAsLong("account_income"), ao = row.getAsLong("account_outcome");
        BigDecimal income = new BigDecimal(row.getAsString("income")),
                outcome = new BigDecimal(row.getAsString("outcome"));

        if (ai == ao) {
            Account.changeBalance(ai, BigDecimal.ZERO, income.subtract(outcome), writeChanged);
        } else {
            Account.changeBalance(ao, BigDecimal.ZERO, outcome.negate(), writeChanged);
            Account.changeBalance(ai, BigDecimal.ZERO, income, writeChanged);
        }
    }
}
