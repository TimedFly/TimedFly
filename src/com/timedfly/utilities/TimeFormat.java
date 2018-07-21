package com.timedfly.utilities;

import com.timedfly.configurations.Languages;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DurationFormatUtils;

import java.util.concurrent.TimeUnit;

public class TimeFormat {

    public static String formatLong(long input) {
        return formatDurationWords(input * 1000, false);
    }

    public static String formatShort(long input, boolean oneWord) {
        input *= 1000;

        long days = TimeUnit.MILLISECONDS.toDays(input);
        long hours = TimeUnit.MILLISECONDS.toHours(input);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(input);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(input);

        if (days > 0) return formatDurationWords(TimeUnit.DAYS.toMillis(days), oneWord);
        else if (hours > 0) return formatDurationWords(TimeUnit.HOURS.toMillis(hours), oneWord);
        else if (minutes > 0) return formatDurationWords(TimeUnit.MINUTES.toMillis(minutes), oneWord);
        else if (seconds > 0) return formatDurationWords(TimeUnit.SECONDS.toMillis(seconds), oneWord);
        else return formatDurationWords(0, false);
    }

    public static String formatOneWorded(long input) {
        return formatDurationWords(input * 1000, true);
    }

    private static String formatDurationWords(long durationMillis, boolean oneWord) {

        String days = !oneWord ? Languages.getFormat("Format.Plural.Days") : "d";
        String hours = !oneWord ? Languages.getFormat("Format.Plural.Hours") : "h";
        String minutes = !oneWord ? Languages.getFormat("Format.Plural.Minutes") : "m";
        String seconds = !oneWord ? Languages.getFormat("Format.Plural.Seconds") : "s";
        String day = !oneWord ? Languages.getFormat("Format.Singular.Day") : "d";
        String hour = !oneWord ? Languages.getFormat("Format.Singular.Hour") : "h";
        String minute = !oneWord ? Languages.getFormat("Format.Singular.Minute") : "m";
        String second = !oneWord ? Languages.getFormat("Format.Singular.Second") : "s";

        String duration = !oneWord ? DurationFormatUtils.formatDuration(durationMillis, "d' " + days + " 'H' " + hours + " 'm' " + minutes + " 's' " + seconds + "'") :
                DurationFormatUtils.formatDuration(durationMillis, "d'" + days + " 'H'" + hours + " 'm'" + minutes + " 's'" + seconds + "'");
        String tmp;
        String zero = !oneWord ? " 0 " : " 0";
        String one = !oneWord ? " 1 " : " 1";

        duration = " " + duration;
        tmp = StringUtils.replaceOnce(duration, zero + days, "");
        if (tmp.length() != duration.length()) {
            duration = tmp;
            tmp = StringUtils.replaceOnce(tmp, zero + hours, "");
            if (tmp.length() != duration.length()) {
                tmp = StringUtils.replaceOnce(tmp, zero + minutes, "");
                duration = tmp;
                if (tmp.length() != tmp.length()) {
                    duration = StringUtils.replaceOnce(tmp, zero + seconds, "");
                }
            }
        }

        if (duration.length() != 0) {
            duration = duration.substring(1);
        }

        tmp = StringUtils.replaceOnce(duration, zero + seconds, "");
        if (tmp.length() != duration.length()) {
            duration = tmp;
            tmp = StringUtils.replaceOnce(tmp, zero + minutes, "");
            if (tmp.length() != duration.length()) {
                duration = tmp;
                tmp = StringUtils.replaceOnce(tmp, zero + hours, "");
                if (tmp.length() != duration.length()) {
                    duration = StringUtils.replaceOnce(tmp, zero + days, "");
                }
            }
        }

        duration = " " + duration;
        duration = StringUtils.replaceOnce(duration, one + seconds, one + second);
        duration = StringUtils.replaceOnce(duration, one + minutes, one + minute);
        duration = StringUtils.replaceOnce(duration, one + hours, one + hour);
        duration = StringUtils.replaceOnce(duration, one + days, one + day);
        return duration.trim();
    }

    public static Long timeToSeconds(String timeFormat) {
        String[] timeBefore = timeFormat.split(" ");
        long time = 0;

        if (StringUtils.isNumeric(timeFormat)) time += Integer.parseInt(timeFormat) * 60;
        else for (String timeString : timeBefore) {
            if (timeString.contains("s")) {
                time += Long.parseLong(timeString.replaceAll("[a-zA-Z]", ""));
            }
            if (timeString.contains("m")) {
                time += Long.parseLong(timeString.replaceAll("[a-zA-Z]", "")) * 60;
            }
            if (timeString.contains("h")) {
                time += Long.parseLong(timeString.replaceAll("[a-zA-Z]", "")) * 3600;
            }
            if (timeString.contains("d")) {
                time += Long.parseLong(timeString.replaceAll("[a-zA-Z]", "")) * 86400;
            }
        }
        return time;
    }
}
