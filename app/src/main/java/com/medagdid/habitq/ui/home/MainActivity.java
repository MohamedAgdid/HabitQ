package com.medagdid.habitq.ui.home;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.medagdid.habitq.R;
import com.medagdid.habitq.data.HabitRepository;
import com.medagdid.habitq.data.UserRepository;
import com.medagdid.habitq.model.Habit;
import com.medagdid.habitq.model.User;
import com.medagdid.habitq.ui.habits.HabitAdapter;
import com.medagdid.habitq.util.TimeUtils;
import com.medagdid.habitq.util.XpManager;
import com.medagdid.habitq.worker.HabitResetWorker;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private ImageView characterImage;
    private TextView levelText;
    private ProgressBar xpProgress;
    private TextView streakText;
    private RecyclerView habitsRecycler;

    private UserRepository userRepository;
    private HabitRepository habitRepository;
    private User user;
    private HabitAdapter habitAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initData();

        // STEP 8b: Schedule daily habit reset at midnight
        scheduleDailyHabitReset();

        setupHabits();
        checkForStreakBreaks();
        renderUser();
    }

    private void initViews() {
        characterImage = findViewById(R.id.characterImage);
        levelText = findViewById(R.id.levelText);
        xpProgress = findViewById(R.id.xpProgress);
        streakText = findViewById(R.id.streakText);
        habitsRecycler = findViewById(R.id.habitsRecycler);
    }

    private void initData() {
        userRepository = new UserRepository(this);
        habitRepository = new HabitRepository(this);
        user = userRepository.getUser();
    }

    private void renderUser() {
        // LEVEL
        levelText.setText("LEVEL " + user.getLevel());

        // XP bar
        int xpForNext = XpManager.xpForNextLevel(user.getLevel());
        xpProgress.setMax(xpForNext);
        xpProgress.setProgress(user.getCurrentXP());

        // Character image by level
        characterImage.setImageResource(getCharacterForLevel(user.getLevel()));

        // GLOBAL STREAK
        streakText.setText("🔥 Streak: " + getGlobalStreak() + " days");
    }

    private int getCharacterForLevel(int level) {
        if (level >= 10) return R.drawable.nancy;
        if (level >= 5) return R.drawable.joyce;
        return R.drawable.johnathan;
    }

    // SUM of all habit streaks → dynamic global streak
    private int getGlobalStreak() {
        List<Habit> habits = habitRepository.getHabits();
        int total = 0;
        for (Habit h : habits) total += h.getStreak();
        return total;
    }

    private void setupHabits() {
        List<Habit> habits = habitRepository.getHabits();

        // If empty, add sample habits
        if (habits.isEmpty()) {
            habits.add(new Habit("1", "Read 10 pages", 20));
            habits.add(new Habit("2", "Workout 15 min", 30));
            habits.add(new Habit("3", "No sugar today", 15));
            habitRepository.saveHabits(habits);
        }

        habitAdapter = new HabitAdapter(habits, habit -> {
            String today = LocalDate.now().toString();

            // STREAK LOGIC
            if (habit.getLastCompletedDate() != null) {
                String yesterday = LocalDate.now().minusDays(1).toString();
                if (habit.getLastCompletedDate().equals(yesterday)) {
                    habit.setStreak(habit.getStreak() + 1);
                } else {
                    habit.setStreak(1);
                }
            } else {
                habit.setStreak(1);
            }

            habit.setLastCompletedDate(today);

            // XP LOGIC
            boolean leveledUp = XpManager.addXp(user, habit.getXpValue());

            // SAVE CHANGES
            habitRepository.saveHabits(habits);
            userRepository.saveUser(user);

            // REFRESH UI
            renderUser();
            habitAdapter.notifyDataSetChanged();
        });

        habitsRecycler.setLayoutManager(new LinearLayoutManager(this));
        habitsRecycler.setAdapter(habitAdapter);
    }

    // STEP 8b: Check for missed days and reset streaks immediately on app launch
    private void checkForStreakBreaks() {
        List<Habit> habits = habitRepository.getHabits();
        boolean updated = false;

        String today = LocalDate.now().toString();
        String yesterday = LocalDate.now().minusDays(1).toString();

        for (Habit habit : habits) {
            if (habit.getLastCompletedDate() != null) {
                if (!habit.getLastCompletedDate().equals(yesterday) &&
                        !habit.getLastCompletedDate().equals(today)) {
                    // STREAK BROKEN
                    habit.setStreak(0);
                    updated = true;

                    // Optional small XP penalty
                    user.setCurrentXP(Math.max(0, user.getCurrentXP() - 5));
                }
            }
        }

        if (updated) {
            habitRepository.saveHabits(habits);
            userRepository.saveUser(user);
            renderUser();
            habitAdapter.notifyDataSetChanged();
        }
    }

    // STEP 8b: Schedule WorkManager to reset habits daily at midnight
    private void scheduleDailyHabitReset() {
        long initialDelay = TimeUtils.getDelayUntilMidnight();

        PeriodicWorkRequest dailyResetWork = new PeriodicWorkRequest.Builder(
                HabitResetWorker.class,
                24, TimeUnit.HOURS
        )
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "daily_habit_reset",
                ExistingPeriodicWorkPolicy.REPLACE,
                dailyResetWork
        );
    }
}
