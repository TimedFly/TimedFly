package com.timedfly.utilities;

import com.timedfly.TimedFly;
import com.timedfly.configurations.Languages;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class TimeFormat {

    private TimedFly plugin;

    public TimeFormat(TimedFly plugin) {
        this.plugin = plugin;
    }

    public void setActionBar(Player player, FileConfiguration config, int millis) {
        plugin.getNMS().sendActionbar(player, Message.color(config.getString("Fly.ActionBar")
                .replace("%timeleft%", formatLong(millis))));
    }

    public static String formatLong(long input) {
        return formatDurationWords(input * 1000);
    }

    public static String formatShort(long duration) {
        duration *= 1000;

        long days = TimeUnit.MILLISECONDS.toDays(duration);
        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);

        if (days > 0) return formatDurationWords(TimeUnit.DAYS.toMillis(days));
        else if (hours > 0) return formatDurationWords(TimeUnit.HOURS.toMillis(hours));
        else if (minutes > 0) return formatDurationWords(TimeUnit.MINUTES.toMillis(minutes));
        else if (seconds > 0) return formatDurationWords(TimeUnit.SECONDS.toMillis(seconds));
        else return formatDurationWords(0);
    }

    private static String formatDurationWords(long durationMillis) {

        String days = Languages.getFormat("Format.Plural.Days");
        String hours = Languages.getFormat("Format.Plural.Hours");
        String minutes = Languages.getFormat("Format.Plural.Minutes");
        String seconds = Languages.getFormat("Format.Plural.Seconds");
        String day = Languages.getFormat("Format.Singular.Day");
        String hour = Languages.getFormat("Format.Singular.Hour");
        String minute = Languages.getFormat("Format.Singular.Minute");
        String second = Languages.getFormat("Format.Singular.Second");

        String duration = DurationFormatUtils.formatDuration(durationMillis, "d' " + days + " 'H' " + hours + " 'm' " + minutes + " 's' " + seconds + "'");
        String tmp;

        duration = " " + duration;
        tmp = StringUtils.replaceOnce(duration, " 0 " + days, "");
        if (tmp.length() != duration.length()) {
            duration = tmp;
            tmp = StringUtils.replaceOnce(tmp, " 0 " + hours, "");
            if (tmp.length() != duration.length()) {
                tmp = StringUtils.replaceOnce(tmp, " 0 " + minutes, "");
                duration = tmp;
                if (tmp.length() != tmp.length()) {
                    duration = StringUtils.replaceOnce(tmp, " 0 " + seconds, "");
                }
            }
        }

        if (duration.length() != 0) {
            duration = duration.substring(1);
        }

        tmp = StringUtils.replaceOnce(duration, " 0 " + seconds, "");
        if (tmp.length() != duration.length()) {
            duration = tmp;
            tmp = StringUtils.replaceOnce(tmp, " 0 " + minutes, "");
            if (tmp.length() != duration.length()) {
                duration = tmp;
                tmp = StringUtils.replaceOnce(tmp, " 0 " + hours, "");
                if (tmp.length() != duration.length()) {
                    duration = StringUtils.replaceOnce(tmp, " 0 " + days, "");
                }
            }
        }

        duration = " " + duration;
        duration = StringUtils.replaceOnce(duration, " 1 " + seconds, " 1 " + second);
        duration = StringUtils.replaceOnce(duration, " 1 " + minutes, " 1 " + minute);
        duration = StringUtils.replaceOnce(duration, " 1 " + hours, " 1 " + hour);
        duration = StringUtils.replaceOnce(duration, " 1 " + days, " 1 " + day);
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
