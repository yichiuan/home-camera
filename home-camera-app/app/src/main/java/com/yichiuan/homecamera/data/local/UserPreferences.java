package com.yichiuan.homecamera.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class UserPreferences {
    private static final String USER_PREF_FILE = "user_pref";
    private static final String KEY_ACCESS_TOKEN = "KEY_ACCESS_TOKEN";

    private final SharedPreferences preferences;

    private String accessToken;
    private boolean isLoggedIn = false;

    public UserPreferences(Context context) {
        preferences = context.getApplicationContext().getSharedPreferences(USER_PREF_FILE, Context
                .MODE_PRIVATE);

        accessToken = preferences.getString(KEY_ACCESS_TOKEN, null);
        isLoggedIn = !TextUtils.isEmpty(accessToken);
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public String getAccessToken() {
        return preferences.getString(KEY_ACCESS_TOKEN, null);
    }

    public void setAccessToken(String accessToken) {
        if (!TextUtils.isEmpty(accessToken)) {
            this.accessToken = accessToken;
            isLoggedIn = true;
            preferences.edit().putString(KEY_ACCESS_TOKEN, accessToken).apply();
        }
    }

    public void clear() {
        isLoggedIn = false;
        accessToken = null;
        preferences.edit().clear().apply();
    }
}
