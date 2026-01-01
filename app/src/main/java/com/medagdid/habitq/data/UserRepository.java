package com.medagdid.habitq.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.medagdid.habitq.model.User;
import com.google.gson.Gson;

public class UserRepository {

    private static final String PREF_NAME = "habit_quest_prefs";
    private static final String KEY_USER = "user_data";

    private final SharedPreferences prefs;
    private final Gson gson = new Gson();

    public UserRepository(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public User getUser() {
        String json = prefs.getString(KEY_USER, null);
        if (json == null) {
            User user = new User();
            saveUser(user);
            return user;
        }
        return gson.fromJson(json, User.class);
    }

    public void saveUser(User user) {
        prefs.edit()
                .putString(KEY_USER, gson.toJson(user))
                .apply();
    }
}
