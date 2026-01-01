package com.medagdid.habitq.util;

import java.time.LocalDate;

public class DateUtils {

    public static String today() {
        return LocalDate.now().toString();
    }

    public static String yesterday() {
        return LocalDate.now().minusDays(1).toString();
    }
}