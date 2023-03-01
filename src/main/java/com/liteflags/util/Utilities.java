package com.liteflags.util;

import java.util.concurrent.TimeUnit;

public class Utilities {
    public static String convertTime(int time) {
        int days = (int) TimeUnit.MINUTES.toDays(time);
        int hours = (int) (TimeUnit.MINUTES.toHours(time) - TimeUnit.DAYS.toHours(days));
        int minutes = (int) (TimeUnit.MINUTES.toMinutes(time) - TimeUnit.HOURS.toMinutes(hours) - TimeUnit.DAYS.toMinutes(days));

        String timeString = formatTime(days, " day");
        timeString += (timeString.length() == 0 ? "" : (hours == 0 ? "" : ", ")) + formatTime(hours, " hour");
        timeString += (timeString.length() == 0 ? "" : (minutes == 0 ? "" : ", ")) + formatTime(minutes, " minute");

        if (timeString.length() == 0) timeString = "0 minutes";

        return timeString;
    }

    private static String formatTime(int value, String s) {
        return switch (value) {
            case 0 -> "";
            case 1 -> value + s;
            default -> value + s + "s";
        };
    }
}
