package com.medagdid.habitq.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimeUtils {

    /**
     * Computes milliseconds from now until next midnight in system default timezone.
     * Useful for scheduling daily workers.
     */
    public static long getDelayUntilMidnight() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());

        // Next midnight
        ZonedDateTime nextMidnight = now.plusDays(1)
                .toLocalDate()
                .atStartOfDay(ZoneId.systemDefault());

        Duration duration = Duration.between(now, nextMidnight);

        return duration.toMillis();
    }
}
