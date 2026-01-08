package com.medagdid.habitq.util;


import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimeUtils {

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
