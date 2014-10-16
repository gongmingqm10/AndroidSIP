package net.gongmingqm10.sipsample;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import net.gongmingqm10.sipsample.data.Account;

public class SipApplication extends Application {

    private final static String preferenceName = "authinfo";

    private final static String fieldUsername = "username";
    private final static String fieldPassword = "password";
    private final static String fieldDomain = "domain";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Account account;

    private static SipApplication instance;

    public static SipApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        sharedPreferences = getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveAccount(String username, String password, String domain) {
        editor.putString(fieldUsername, username);
        editor.putString(fieldPassword, password);
        editor.putString(fieldDomain, domain);
        editor.commit();
    }

    public void clearAccount() {
        account = null;
        editor.clear();
        editor.commit();
    }

    public Account getAccount() {
        String username = sharedPreferences.getString(fieldUsername, "");
        String password = sharedPreferences.getString(fieldPassword, "");
        String domain = sharedPreferences.getString(fieldDomain, "");
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(domain))
            return null;
        if (account == null) {
            account = new Account(username, password, domain);
        }
        return account;
    }

}
