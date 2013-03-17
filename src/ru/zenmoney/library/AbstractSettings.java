package ru.zenmoney.library;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created with IntelliJ IDEA.
 * User: chedim
 * Date: 16.03.13
 * Time: 23:14
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractSettings {
    protected static SharedPreferences settings;

    public AbstractSettings(Context context) {
        settings = context.getSharedPreferences(getSettingsName(), 0);
    }

    public abstract String getSettingsName();

    public String getString(String key) {
        return settings.getString(key, null);
    }

    public void put(String key, String val) {
        SharedPreferences.Editor edit = settings.edit();
        edit.putString(key, val);
        edit.commit();
    }

    public void put(String key, Long val) {
        SharedPreferences.Editor edit = settings.edit();
        edit.putLong(key, val);
        edit.commit();
    }
}
