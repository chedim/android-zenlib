android-zenlib
==============

Библиотека интеграции с zenmoney.ru

версия: 0.1

На данный момент поддерживает:
- [x] Создание БД со структурой, достаточной для добавления/редактирования транзакций, категорий, счетов
— [x] Синхронизацию валют
— [x] Синхронизацию БД через /v2/sync

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
