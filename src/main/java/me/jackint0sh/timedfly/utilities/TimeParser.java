package me.jackint0sh.timedfly.utilities;

import java.util.Arrays;
import java.util.Stack;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class TimeParser {

    private static String[] timeString = {"seconds", "minutes", "hours", "days"};

    public static int parse(String toParse) throws TimeParser.TimeFormatException {
        return parse(toParse, true);
    }

    public static boolean isParsable(String toParse) {
        try {
            return parse(toParse, false) == -1;
        } catch (TimeFormatException e) {
            return false;
        }
    }

    public static int parse(String toParse, boolean sum) throws TimeParser.TimeFormatException {
        toParse = toParse.replaceAll("\\s", "").toLowerCase();

        if (!toParse.replaceAll("\\d+", "").matches("[a-z]+")) return Integer.parseInt(toParse);

        if (!TimeParser.isNumeric(toParse.charAt(0)))
            throw new TimeParser.TimeFormatException("First char should be a number found: " + toParse.charAt(0));
        if (TimeParser.isNumeric(toParse.charAt(toParse.length() - 1)))
            throw new TimeParser.TimeFormatException("String does not end with a time string");

        Stack<String> intStack = new Stack<>();
        Stack<String> timeStack = new Stack<>();
        int result = 0;

        fillStacks(new StringBuilder(toParse), intStack, timeStack);

        if (!sum) return -1;

        while (!intStack.empty() && !timeStack.empty()) {
            int integer = Integer.parseInt(intStack.pop());
            String time = timeStack.pop();

            result += timeMs(integer, time);
        }

        return result;
    }

    public static int toTicks(String toParse) throws TimeParser.TimeFormatException {
        return parse(toParse) / 50;
    }

    public static int ticksToMs(int ticks) throws TimeParser.TimeFormatException {
        return ticks * 50;
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

    private static long timeMs(int integer, String string) {
        AtomicReference<Long> time = new AtomicReference<>((long) 0);

        Arrays.stream(timeString).filter(value -> value.startsWith(string))
                .forEach(value -> time.set(TimeUnit.valueOf(value.toUpperCase()).toMillis(integer)));

        return time.get();
    }

    public static boolean isNumeric(String s) {
        try {
            Integer.parseInt(s);
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
