package com.medagdid.habitq.ui.home;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.medagdid.habitq.R;
import com.medagdid.habitq.data.UserRepository;
import com.medagdid.habitq.model.User;
import com.medagdid.habitq.model.Habit;
import com.medagdid.habitq.ui.habits.HabitAdapter;
import com.medagdid.habitq.util.XpManager;

public class MainActivity extends AppCompatActivity {

    private ImageView characterImage;
    private TextView levelText;
    private ProgressBar xpProgress;
    private TextView streakText;
    private RecyclerView habitsRecycler;

    private UserRepository userRepository;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initData();
        setupHabits();   // ← Don't forget this
        renderUser();
    }

    private void initViews() {
        characterImage = findViewById(R.id.characterImage);
        levelText = findViewById(R.id.levelText);
        xpProgress = findViewById(R.id.xpProgress);
        streakText = findViewById(R.id.streakText);
        habitsRecycler = findViewById(R.id.habitsRecycler); // fixed
    }

    private void initData() {
        userRepository = new UserRepository(this);
        user = userRepository.getUser();
    }

    private void renderUser() {
        // Level text
        levelText.setText("LEVEL " + user.getLevel());

        // XP bar
        int xpForNext = XpManager.xpForNextLevel(user.getLevel());
        xpProgress.setMax(xpForNext);
        xpProgress.setProgress(user.getCurrentXP());

        // Character image by level
        characterImage.setImageResource(getCharacterForLevel(user.getLevel()));

        // Streak (global for now – later per habit)
        streakText.setText("🔥 Streak: " + getGlobalStreak() + " days");
    }

    private int getCharacterForLevel(int level) {
        if (level >= 10) {
            return R.drawable.nancy;
        } else if (level >= 5) {
            return R.drawable.joyce;
        } else {
            return R.drawable.johnathan;
        }
    }

    private int getGlobalStreak() {
        // TODO: Later compute total streak from all habits
        return 0;
    }

    private void setupHabits() {
        List<Habit> habits = new ArrayList<>();

        habits.add(new Habit("1", "Read 10 pages", 20));
        habits.add(new Habit("2", "Workout 15 min", 30));
        habits.add(new Habit("3", "No sugar today", 15));

        HabitAdapter adapter = new HabitAdapter(habits, habit -> {
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

            // XP LOGIC using XpManager
            boolean leveledUp = XpManager.addXp(user, habit.getXpValue());

            // SAVE USER
            userRepository.saveUser(user);

            // UPDATE UI
            renderUser();
        });

        habitsRecycler.setLayoutManager(new LinearLayoutManager(this));
        habitsRecycler.setAdapter(adapter);
    }
}
