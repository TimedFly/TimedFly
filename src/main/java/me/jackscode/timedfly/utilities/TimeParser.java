package me.jackscode.timedfly.utilities;

import java.util.*;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;

public class TimeParser {

    private TimeParser() {}

    private static final Map<String, String> timeLang = new HashMap<>();
    private static final Collection<String> timeString = timeLang.values();

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

        Deque<String> intStack = new ArrayDeque<>();
        Deque<String> timeStack = new ArrayDeque<>();
        long result = 0;

        fillStacks(new StringBuilder(toParse), intStack, timeStack);

        if (!sum) return -1;

        while (!intStack.isEmpty() && !timeStack.isEmpty()) {
            long integer = Integer.parseInt(intStack.pop());
            String time = timeStack.pop();

            result += getTimeInSeconds(integer, time);
        }

        return result;
    }

    public static String toReadableString(long ms) {
        return toReadableString(ms, true);
    }

    public static String toReadableString(long ms, boolean longWord) {
        return toReadableString(ms, longWord, false);
    }

    @NotNull
    public static String toReadableString(long ms, boolean longWord, boolean cut) {
        StringBuilder result = new StringBuilder();
        long time = ms;

        long days = TimeUnit.MILLISECONDS.toDays(time);
        if (days > 0) {
            result.append(days);

            final String plural = timeLang.getOrDefault("plural.days", "days");
            final String singular = timeLang.getOrDefault("singular.days", "day");
            final String sshort = timeLang.getOrDefault("short.days", "d");

            if (longWord) result.append(" ").append(days > 1 ? plural : singular).append(" ");
            else result.append(sshort).append(" ");

            time -= TimeUnit.DAYS.toMillis(days);
            if (cut) return result.toString().trim();
        }

        long hours = TimeUnit.MILLISECONDS.toHours(time);
        if (hours > 0) {
            result.append(hours);

            final String plural = timeLang.getOrDefault("plural.hours", "hours");
            final String singular = timeLang.getOrDefault("singular.hours", "hour");
            final String sshort = timeLang.getOrDefault("short.hours", "h");

            if (longWord) result.append(" ").append(hours > 1 ? plural : singular).append(" ");
            else result.append(sshort).append(" ");

            time -= TimeUnit.HOURS.toMillis(hours);
            if (cut) return result.toString().trim();
        }

        long minutes = TimeUnit.MILLISECONDS.toMinutes(time);
        if (minutes > 0) {
            result.append(minutes);

            final String plural = timeLang.getOrDefault("plural.minutes", "minutes");
            final String singular = timeLang.getOrDefault("singular.minutes", "minute");
            final String sshort = timeLang.getOrDefault("short.minutes", "m");

            if (longWord) result.append(" ").append(minutes > 1 ? plural : singular).append(" ");
            else result.append(sshort).append(" ");

            time -= TimeUnit.MINUTES.toMillis(minutes);
            if (cut) return result.toString().trim();
        }

        long seconds = TimeUnit.MILLISECONDS.toSeconds(time);
        if (seconds > 0) {
            result.append(seconds);

            final String plural = timeLang.getOrDefault("plural.seconds", "seconds");
            final String singular = timeLang.getOrDefault("singular.seconds", "second");
            final String sshort = timeLang.getOrDefault("short.seconds", "s");

            if (longWord) result.append(" ").append(seconds > 1 ? plural : singular).append(" ");
            else result.append(sshort).append(" ");
        }

        String readableString = result.toString().trim();

        if (!readableString.isEmpty()) {
            return readableString;
        }

        return "0";
    }

    public static long toSeconds(long ms) {
        return TimeUnit.MILLISECONDS.toSeconds(ms);
    }

    public static long secondsToMs(long seconds) {
        return TimeUnit.SECONDS.toMillis(seconds);
    }

    private static void fillStacks(StringBuilder string, Deque<String> intStack, Deque<String> timeStack) throws TimeParser.TimeFormatException {
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
        if (!isTimeString(builderString)) throw new TimeParser.TimeFormatException("Invalid time string provided: " + builderString);
        return builderString;
    }

    private static long getTimeInSeconds(long integer, String string) {
        Collection<String> timeCollection = timeString;

        if (timeCollection.isEmpty()) {
            timeCollection = Arrays.asList("seconds", "minutes", "hours", "days");
        }

        return timeCollection.stream()
                .filter(value -> value.startsWith(string))
                .mapToLong(value -> TimeUnit.valueOf(value.toUpperCase()).toSeconds(integer))
                .sum();
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
        Collection<String> timeCollection = timeString;

        if (timeCollection.isEmpty()) {
            timeCollection = Arrays.asList("seconds", "minutes", "hours", "days");
        }

        return timeCollection.stream().anyMatch(string -> string.startsWith(s));
    }

    public static void setTimeLang(String timePath, String timeString) {
        String[] string = {"plural", "singular", "short"};
        if (Arrays.stream(string).anyMatch(timePath::startsWith)) {
            throw new IllegalArgumentException(
                    "The path must start with one of the following: " + String.join(", ", string)
            );
        }
        timeLang.put(timePath, timeString);
    }

    public static class TimeFormatException extends Exception {
        TimeFormatException(String message) {
            super(message);
        }
    }
}