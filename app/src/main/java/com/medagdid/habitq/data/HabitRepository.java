package com.medagdid.habitq.data;


import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.medagdid.habitq.model.Habit;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HabitRepository {

    private static final String PREF_NAME = "habit_quest_prefs";
    private static final String KEY_HABITS = "habits_list";

    private final SharedPreferences prefs;
    private final Gson gson = new Gson();
    private final Type listType = new TypeToken<List<Habit>>() {}.getType();

    public HabitRepository(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public List<Habit> getHabits() {
        String json = prefs.getString(KEY_HABITS, null);
        if (json == null) return new ArrayList<>();
        return gson.fromJson(json, listType);
    }

    public void saveHabits(List<Habit> habits) {
        prefs.edit()
                .putString(KEY_HABITS, gson.toJson(habits))
                .apply();
    }

    public void addHabit(Habit habit) {
        List<Habit> habits = getHabits();
        habits.add(habit);
        saveHabits(habits);
    }

    public void deleteHabit(String habitId) {
        List<Habit> habits = getHabits();
        habits.removeIf(h -> h.getId().equals(habitId));
        saveHabits(habits);
    }
}
