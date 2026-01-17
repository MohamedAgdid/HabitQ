package com.medagdid.habitq.ui.stats;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.medagdid.habitq.R;
import com.medagdid.habitq.data.HabitRepository;
import com.medagdid.habitq.model.Habit;

import java.util.List;

public class StatisticsActivity extends AppCompatActivity {

    private TextView totalHabitsText;
    private TextView completedHabitsText;
    private ProgressBar habitProgress;
    private TextView progressPercentage;

    private List<Habit> habits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        totalHabitsText = findViewById(R.id.totalHabits);
        completedHabitsText = findViewById(R.id.completedHabits);
        habitProgress = findViewById(R.id.habitProgress);
        progressPercentage = findViewById(R.id.progressPercentage);

        HabitRepository habitRepository = new HabitRepository(this);
        habits = habitRepository.getHabits();

        showStatistics();
    }

    private void showStatistics() {
        int total = habits.size();
        int completed = 0;

        for (Habit habit : habits) {
            if (habit.isCompletedToday()) completed++;
        }

        totalHabitsText.setText("Total habits: " + total);
        completedHabitsText.setText("Completed habits: " + completed);

        int progress = total == 0 ? 0 : (completed * 100 / total);
        habitProgress.setProgress(progress);
        progressPercentage.setText(progress + "%");
    }
}
