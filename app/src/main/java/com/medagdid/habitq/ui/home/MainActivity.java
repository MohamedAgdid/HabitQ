package com.medagdid.habitq.ui.home;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.medagdid.habitq.R;
import com.medagdid.habitq.data.HabitRepository;
import com.medagdid.habitq.data.UserRepository;
import com.medagdid.habitq.model.Habit;
import com.medagdid.habitq.model.User;
import com.medagdid.habitq.ui.habits.HabitAdapter;
import com.medagdid.habitq.util.TimeUtils;
import com.medagdid.habitq.util.XpManager;
import com.medagdid.habitq.worker.HabitResetWorker;

import com.medagdid.habitq.ui.stats.StatisticsActivity;

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
    private List<Habit> habits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initData();
        setupHabits();

        findViewById(R.id.addNewHabit)
                .setOnClickListener(v -> showAddHabitDialog());

        findViewById(R.id.iconStats).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
            startActivity(intent);
        });

        checkForStreakBreaks();
        scheduleDailyHabitReset();
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


    private void setupHabits() {
        habits = habitRepository.getHabits();

        habitAdapter = new HabitAdapter(habits, new HabitAdapter.OnHabitActionListener() {
            @Override
            public void onHabitCompleted(Habit habit) {
                handleHabitCompletion(habit);
            }

            @Override
            public void onHabitDelete(Habit habit) {
                showDeleteDialog(habit);
            }
        });

        habitsRecycler.setLayoutManager(new LinearLayoutManager(this));
        habitsRecycler.setAdapter(habitAdapter);
    }

    private void handleHabitCompletion(Habit habit) {
        String today = LocalDate.now().toString();
        String yesterday = LocalDate.now().minusDays(1).toString();

        if (habit.getLastCompletedDate() != null &&
                habit.getLastCompletedDate().equals(yesterday)) {
            habit.setStreak(habit.getStreak() + 1);
        } else {
            habit.setStreak(1);
        }

        habit.setLastCompletedDate(today);
        XpManager.addXp(user, habit.getXpValue());

        habitRepository.saveHabits(habits);
        userRepository.saveUser(user);

        renderUser();
    }

    private void showDeleteDialog(Habit habit) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete habit")
                .setMessage("Delete \"" + habit.getName() + "\"?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (d, w) -> deleteHabit(habit))
                .show();
    }

    private void deleteHabit(Habit habit) {
        int index = habits.indexOf(habit);
        if (index == -1) return;

        habitRepository.deleteHabit(habit.getId());
        habits.remove(index);
        habitAdapter.notifyItemRemoved(index);

        renderUser();
    }


    private void renderUser() {
        levelText.setText("LEVEL " + user.getLevel());

        int xpForNext = XpManager.xpForNextLevel(user.getLevel());
        xpProgress.setMax(xpForNext);
        xpProgress.setProgress(user.getCurrentXP());

        characterImage.setImageResource(getCharacterForLevel(user.getLevel()));
        streakText.setText("🔥 Streak: " + getGlobalStreak() + " days");
    }

    private int getCharacterForLevel(int level) {
        if (level >= 10) return R.drawable.nancy;
        if (level >= 5) return R.drawable.joyce;
        return R.drawable.johnathan;
    }

    private int getGlobalStreak() {
        int total = 0;
        for (Habit h : habits) total += h.getStreak();
        return total;
    }
    

    private void showAddHabitDialog() {
        View dialogView = getLayoutInflater()
                .inflate(R.layout.dialog_add_habit, null);

        EditText input = dialogView.findViewById(R.id.habitNameInput);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Add Habit")
                .setView(dialogView)
                .setPositiveButton("Add", (d, w) -> {
                    String name = input.getText().toString().trim();
                    if (name.isEmpty()) return;

                    Habit habit = new Habit(
                            String.valueOf(System.currentTimeMillis()),
                            name,
                            20
                    );

                    habitRepository.addHabit(habit);
                    habits.add(habit);
                    habitAdapter.notifyItemInserted(habits.size() - 1);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void checkForStreakBreaks() {
        String today = LocalDate.now().toString();
        String yesterday = LocalDate.now().minusDays(1).toString();

        boolean updated = false;

        for (Habit habit : habits) {
            if (habit.getLastCompletedDate() != null &&
                    !habit.getLastCompletedDate().equals(today) &&
                    !habit.getLastCompletedDate().equals(yesterday)) {

                habit.setStreak(0);
                updated = true;
            }
        }

        if (updated) {
            habitRepository.saveHabits(habits);
            renderUser();
        }
    }

    private void scheduleDailyHabitReset() {
        long delay = TimeUtils.getDelayUntilMidnight();

        PeriodicWorkRequest work =
                new PeriodicWorkRequest.Builder(
                        HabitResetWorker.class,
                        24, TimeUnit.HOURS
                )
                        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                        .build();

        WorkManager.getInstance(this)
                .enqueueUniquePeriodicWork(
                        "daily_habit_reset",
                        ExistingPeriodicWorkPolicy.REPLACE,
                        work
                );
    }
}
