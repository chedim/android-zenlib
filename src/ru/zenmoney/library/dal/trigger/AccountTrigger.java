package ru.zenmoney.library.dal.trigger;

import android.content.ContentValues;
import ru.zenmoney.library.dal.RowSet;
import ru.zenmoney.library.dal.model.ConnectionData;
import ru.zenmoney.library.dal.model.Transaction;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: chedim
 * Date: 16.03.13
 * Time: 21:36
 * To change this template use File | Settings | File Templates.
 */
public class AccountTrigger extends AbstractTrigger {
    @Override
    public void onBeforeDelete(ContentValues row, boolean writeChanged) throws Exception  {
        Long id = row.getAsLong("_id");
        RowSet transactions = new RowSet(Transaction.class);
        String sid = row.getAsString("_id");
        String[] args = {sid, sid};
        transactions.addQueryResult("account_income = ? OR account_outcome = ?", args);
        Transaction transaction;
        Long tai, tao;
        for (Iterator<Transaction> it = transactions.iterator(); it.hasNext();  ) {
            transaction = it.next();
            tai = transaction.getAsLong("account_income");
            tao = transaction.getAsLong("account_outcome");
            if (tai == tao) {
                transaction.delete(writeChanged);
            } else if (tai == id) {
                transaction.put("account_income", tao);
                transaction.setSum(transaction.getAsBigDecimal("outcome").negate());
                transaction.save(writeChanged);
            } else {
                transaction.put("account_outcome", tai);
                transaction.setSum(transaction.getAsBigDecimal("income"));
                transaction.save(writeChanged);
            }
        }

        RowSet cds = new RowSet(ConnectionData.class);
        cds.addQueryResult("account = ?", new String[]{sid});
        cds.delete(writeChanged);
    }
}
