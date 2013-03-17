android-zenlib
==============

Библиотека интеграции с zenmoney.ru

версия: 0.1

На данный момент поддерживает:
- [x] Создание БД со структурой, достаточной для добавления/редактирования транзакций, категорий, счетов
- [x] Синхронизацию валют
- [x] Синхронизацию БД через /v2/sync

Поддержка: chedim@chedim.ru

## Включенные зависимости: ##
* commons-codec-1.7
* httpcore-4.2.3
* jackson-core-2.1.4
* pojava-2.8.1
* scribe OAuth

## Примеры использования: ##

### Авторизация ###
```java
public class AuthActivity extends ru.zenmoney.library.api.AuthActivity {

    @Override
    protected void onComplete(Token consumer) {
        Utils.message(this, "Auth OK! %)");
        OAuthSettings settings = new OAuthSettings(this);
        settings.setToken(consumer);
        Intent i = new Intent(this, StartActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onError(Token requestToken, String url, Throwable error) {
        Log.e("auth", "Auth failed", error);
        Utils.alert(this, "Auth failed", error.getMessage());
        Intent i = new Intent(this, StartActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onRequestDeclined() {
        Utils.message(this, "Auth declined");
        Intent i = new Intent(this, StartActivity.class);
        startActivity(i);
        finish();
    }
}

```

### Синхронизация ###
```java
    Class<? extends AbstractModel>[] structure = new Class[]{
            User.class,
            Account.class,
            Connection.class,
            ConnectionData.class,
            Tag.class,
            TagGroup.class,
            Transaction.class,
            TransactionTag.class,
//            InstrumentRate.class
    };

    ZenmoneyApi.sync(structure);
```

### Добавление объекта ###
```java
    Account account = new Account();
    account.put("title", "Первый счет");
    account.put("instrument", 2l);
    account.put("balance", BigDecimal.ZERO);
    account.put("type", "cash");
    account.save();
    Transaction transaction = new Transaction();
    transaction.put("account_income", account.getKey());
    transaction.put("account_outcome", account.getKey());
    transaction.put("income", new BigDecimal("1000000"));
    transaction.put("outcome", BigDecimal.ZERO);
    transaction.put("comment", "Я миллионер!");
    transaction.save();
```

### Поиск объектов ###
```java
    RowSet transactions = new RowSet(Transaction.class);
    RowSet.addQueryResult("account_income = ?", new String[] {"1"});
```

```java
    Transaction transaction = new Transaction();
    transaction.loadByKey(1);
```

```java
    Cursor c = DatabaseHelper.getWritableConnection().rawQuery("SELECT * FROM `transaction` WHERE _id = 1");
    Transaction transaction = new Transaction(c);
```

### Удаление объектов ###
```java
    Transaction transaction = new Transaction();
    transaction.loadByKey(1);
    transaction.delete();
```

```java
    RowSet transactions = new RowSet(Transaction.class);
    RowSet.addQueryResult("account_income = ?", new String[] {"1"});
    rowset.delete();
```

