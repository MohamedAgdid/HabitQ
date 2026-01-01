package com.medagdid.habitq.worker;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.medagdid.habitq.data.HabitRepository;
import com.medagdid.habitq.data.UserRepository;
import com.medagdid.habitq.model.Habit;
import com.medagdid.habitq.model.User;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class HabitResetWorker extends Worker {

    public HabitResetWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        HabitRepository habitRepo = new HabitRepository(getApplicationContext());
        UserRepository userRepo = new UserRepository(getApplicationContext());

        List<Habit> habits = habitRepo.getHabits();
        User user = userRepo.getUser();

        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        LocalDate yesterday = today.minusDays(1);

        for (Habit habit : habits) {
            if (habit.getLastCompletedDate() == null) continue;

            LocalDate lastCompleted = LocalDate.parse(habit.getLastCompletedDate());

            if (!lastCompleted.equals(yesterday) && !lastCompleted.equals(today)) {
                // Streak broken
                habit.setStreak(0);

                // Optional XP penalty
                user.setCurrentXP(Math.max(0, user.getCurrentXP() - 5));
            }
        }

        // Save changes
        habitRepo.saveHabits(habits);
        userRepo.saveUser(user);

        return Result.success();
    }
}

