package com.example.trafficandroidapp.security;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "auth_prefs";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_AVATAR = "user_avatar"; // Nueva clave

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveSession(String token, String name, String email, String avatar) {
        prefs.edit()
                .putString(KEY_TOKEN, token)
                .putString(KEY_USER_NAME, name)
                .putString(KEY_USER_EMAIL, email)
                .putString(KEY_USER_AVATAR, avatar)
                .apply();
    }

    public String getToken() { return prefs.getString(KEY_TOKEN, null); }
    public String getUserName() { return prefs.getString(KEY_USER_NAME, ""); }
    public String getUserEmail() { return prefs.getString(KEY_USER_EMAIL, ""); }
    public String getUserAvatar() { return prefs.getString(KEY_USER_AVATAR, ""); }

    public void updateUserName(String name) {
        prefs.edit().putString(KEY_USER_NAME, name).apply();
    }

    public void updateAvatar(String avatarUrl) {
        prefs.edit().putString(KEY_USER_AVATAR, avatarUrl).apply();
    }

    public void clear() { prefs.edit().clear().apply(); }
}