package com.liteflags.util;

import java.time.Duration;

public class Utilities {
    public static String convertTime(Duration time) {
        int days = Math.abs((int) time.toDaysPart());
        int hours = Math.abs(time.toHoursPart());
        int minutes = Math.abs(time.toMinutesPart());

        String timeString = formatTime(days, " day");
        timeString += (timeString.length() == 0 ? "" : (hours == 0 ? "" : ", ")) + formatTime(hours, " hour");
        timeString += (timeString.length() == 0 ? "" : (minutes == 0 ? "" : ", ")) + formatTime(minutes, " minute");

        if (timeString.length() == 0)
            timeString = "0 minutes";

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
