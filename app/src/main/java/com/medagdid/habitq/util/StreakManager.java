package com.medagdid.habitq.util;

import com.medagdid.habitq.model.Habit;

public class StreakManager {

    public static boolean canCompleteToday(Habit habit) {
        String lastDate = habit.getLastCompletedDate();
        return lastDate == null || !lastDate.equals(DateUtils.today());
    }

    public static int updateStreak(Habit habit) {
        String lastDate = habit.getLastCompletedDate();

        if (lastDate == null) {
            habit.setStreak(1);
        }
        else if (lastDate.equals(DateUtils.yesterday())) {
            habit.setStreak(habit.getStreak() + 1);
        }
        else {
            habit.setStreak(1);
        }

        habit.setLastCompletedDate(DateUtils.today());
        return habit.getStreak();
    }
}
