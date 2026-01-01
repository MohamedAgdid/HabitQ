package com.medagdid.habitq.model;
import java.time.LocalDate;

public class Habit {

    private String id;
    private String name;
    private int streak;
    private String lastCompletedDate; // yyyy-MM-dd
    private int xpValue;

    public Habit(String id, String name, int xpValue) {
        this.id = id;
        this.name = name;
        this.xpValue = xpValue;
        this.streak = 0;
        this.lastCompletedDate = null;
    }

    // Getters & setters
    public String getId() { return id; }
    public String getName() { return name; }
    public int getStreak() { return streak; }
    public String getLastCompletedDate() { return lastCompletedDate; }
    public int getXpValue() { return xpValue; }

    public void setStreak(int streak) { this.streak = streak; }
    public void setLastCompletedDate(String date) { this.lastCompletedDate = date; }

    public boolean isCompletedToday() {
        if (lastCompletedDate == null) return false;

        String today = LocalDate.now().toString(); // yyyy-MM-dd
        return today.equals(lastCompletedDate);
    }
}

