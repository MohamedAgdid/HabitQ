package com.medagdid.habitq.ui.home;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.medagdid.habitq.R;
import com.medagdid.habitq.data.UserRepository;
import com.medagdid.habitq.model.User;
import com.medagdid.habitq.util.XpManager;

public class MainActivity extends AppCompatActivity {

    private ImageView characterImage;
    private TextView levelText;
    private ProgressBar xpProgress;
    private TextView streakText;

    private UserRepository userRepository;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initData();
        renderUser();
    }

    private void initViews() {
        characterImage = findViewById(R.id.characterImage);
        levelText = findViewById(R.id.levelText);
        xpProgress = findViewById(R.id.xpProgress);
        streakText = findViewById(R.id.streakText);
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
            return R.drawable.Nancy;
        } else if (level >= 5) {
            return R.drawable.Joyce;
        } else {
            return R.drawable.Johnathan;
        }
    }

    private int getGlobalStreak() {
        // MVP: placeholder
        // Later we compute from habits
        return 0;
    }
}
