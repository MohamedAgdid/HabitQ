package com.medagdid.habitq.util;
import com.medagdid.habitq.model.User;
public class XpManager {
    public static int xpForNextLevel(int level) {
        return 100 + (level * 50);
    }

    public static boolean addXp(User user, int xpGained) {
        user.addXP(xpGained);

        boolean leveledUp = false;

        while (user.getCurrentXP() >= xpForNextLevel(user.getLevel())) {
            user.setCurrentXP(
                    user.getCurrentXP() - xpForNextLevel(user.getLevel())
            );
            user.setLevel(user.getLevel() + 1);
            leveledUp = true;
        }

        return leveledUp;
    }
}
