package com.medagdid.habitq.util;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimeUtils {

    /**
     * Calculate milliseconds from now until next midnight in system default timezone.
     */
    public static long getDelayUntilMidnight() {
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
        LocalDateTime nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay();
        Duration duration = Duration.between(now, nextMidnight);
        return duration.toMillis();
    }
}
