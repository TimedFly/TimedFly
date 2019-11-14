package me.jackint0sh.timedfly.utilities;

import java.util.Arrays;
import java.util.Stack;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class TimeParser {

    private static String[] timeString = {"seconds", "minutes", "hours", "days"};

    public static long parseNoException(String toParse) {
        try {
            return parse(toParse, true);
        } catch (TimeFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static long parse(String toParse) throws TimeParser.TimeFormatException {
        return parse(toParse, true);
    }

    public static boolean isParsable(String toParse) {
        try {
            return parse(toParse, false) == -1;
        } catch (TimeFormatException e) {
            return false;
        }
    }

    public static long parse(String toParse, boolean sum) throws TimeParser.TimeFormatException {
        toParse = toParse.replaceAll("\\s", "").toLowerCase();

        if (TimeParser.isNumeric(toParse)) return Long.parseLong(toParse);

        if (!TimeParser.isNumeric(toParse.charAt(0)))
            throw new TimeParser.TimeFormatException("First char should be a number found: " + toParse.charAt(0));
        if (TimeParser.isNumeric(toParse.charAt(toParse.length() - 1)))
            throw new TimeParser.TimeFormatException("String does not end with a time string");

        Stack<String> intStack = new Stack<>();
        Stack<String> timeStack = new Stack<>();
        long result = 0;

        fillStacks(new StringBuilder(toParse), intStack, timeStack);

        if (!sum) return -1;

        while (!intStack.empty() && !timeStack.empty()) {
            long integer = Integer.parseInt(intStack.pop());
            String time = timeStack.pop();

            result += timeMs(integer, time);
        }

        return result;
    }

    public static String toReadableString(long ms) {
        return toReadableString(ms, true);
    }

    public static String toReadableString(long ms, boolean longWord) {
        return toReadableString(ms, longWord, false);
    }

    public static String toReadableString(long ms, boolean longWord, boolean cut) {
        StringBuilder result = new StringBuilder();
        long time = ms;

        long days = TimeUnit.MILLISECONDS.toDays(time);
        if (days > 0) {
            result.append(days);

            final String plural = Languages.getString("time_string.plural.days");
            final String singular = Languages.getString("time_string.singular.days");
            final String sshort = Languages.getString("time_string.short.days");

            if (longWord) result.append(" ").append(days > 1 ? plural : singular).append(" ");
            else result.append(sshort).append(" ");

            time -= TimeUnit.DAYS.toMillis(days);
            if (cut) return result.toString().trim();
        }

        long hours = TimeUnit.MILLISECONDS.toHours(time);
        if (hours > 0) {
            result.append(hours);

            final String plural = Languages.getString("time_string.plural.hours");
            final String singular = Languages.getString("time_string.singular.hours");
            final String sshort = Languages.getString("time_string.short.hours");

            if (longWord) result.append(" ").append(hours > 1 ? plural : singular).append(" ");
            else result.append(sshort).append(" ");

            time -= TimeUnit.HOURS.toMillis(hours);
            if (cut) return result.toString().trim();
        }

        long minutes = TimeUnit.MILLISECONDS.toMinutes(time);
        if (minutes > 0) {
            result.append(minutes);

            final String plural = Languages.getString("time_string.plural.minutes");
            final String singular = Languages.getString("time_string.singular.minutes");
            final String sshort = Languages.getString("time_string.short.minutes");

            if (longWord) result.append(" ").append(minutes > 1 ? plural : singular).append(" ");
            else result.append(sshort).append(" ");

            time -= TimeUnit.MINUTES.toMillis(minutes);
            if (cut) return result.toString().trim();
        }

        long seconds = TimeUnit.MILLISECONDS.toSeconds(time);
        if (seconds > 0) {
            result.append(seconds);

            final String plural = Languages.getString("time_string.plural.seconds");
            final String singular = Languages.getString("time_string.singular.seconds");
            final String sshort = Languages.getString("time_string.short.seconds");

            if (longWord) result.append(" ").append(seconds > 1 ? plural : singular).append(" ");
            else result.append(sshort).append(" ");
        }

        return result.toString().trim();
    }

    public static long toSeconds(long ms) {
        return TimeUnit.MILLISECONDS.toSeconds(ms);
    }

    public static long secondsToMs(long seconds) {
        return TimeUnit.SECONDS.toMillis(seconds);
    }

    private static void fillStacks(StringBuilder string, Stack<String> intStack, Stack<String> timeStack) throws TimeParser.TimeFormatException {
        if (string.length() != 0) {
            intStack.push(findInteger(string));
            timeStack.push(findTimeString(string));
            fillStacks(string, intStack, timeStack);
        }
    }

    private static String findInteger(StringBuilder string) {
        StringBuilder integer = new StringBuilder();
        int i = 0;

        while (i < string.length()) {
            String charac = String.valueOf(string.charAt(i));
            if (!TimeParser.isNumeric(charac)) break;
            integer.append(charac);
            i++;
        }
        string.delete(0, i);
        return integer.toString();
    }

    private static String findTimeString(StringBuilder string) throws TimeParser.TimeFormatException {
        StringBuilder time = new StringBuilder();
        int i = 0;

        while (i < string.length()) {
            String charac = String.valueOf(string.charAt(i));
            if (TimeParser.isNumeric(charac)) break;
            time.append(charac);
            i++;
        }
        string.delete(0, i);

        String builderString = time.toString();
        if (!isTimeString(builderString)) throw new TimeParser.TimeFormatException("Invalid time string provided");
        return builderString;
    }

    private static long timeMs(long integer, String string) {
        AtomicReference<Long> time = new AtomicReference<>((long) 0);

        Arrays.stream(timeString).filter(value -> value.startsWith(string))
                .forEach(value -> time.set(TimeUnit.valueOf(value.toUpperCase()).toMillis(integer)));

        return time.get();
    }

    public static boolean isNumeric(String s) {
        try {
            Long.parseLong(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isNumeric(char c) {
        return isNumeric(String.valueOf(c));
    }

    public static boolean isTimeString(String s) {
        return Arrays.stream(timeString).anyMatch(string -> string.startsWith(s));
    }

    public static class TimeFormatException extends Exception {
        TimeFormatException(String message) {
            super(message);
        }
    }

}
