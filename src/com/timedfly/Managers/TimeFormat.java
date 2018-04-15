package com.timedfly.Managers;

import com.timedfly.ConfigurationFiles.LangFiles;
import com.timedfly.TimedFly;
import com.timedfly.Utilities.Utility;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class TimeFormat {

    private TimedFly plugin;

    public TimeFormat(TimedFly plugin) {
        this.plugin = plugin;
    }

    public void setActionBar(Player player, FileConfiguration config, int millis) {
        plugin.getNMS().sendActionbar(player, Utility.color(config.getString("Fly.ActionBar.Message")
                .replace("%timeleft%", format(millis))));
    }

    public static String format(long input) {
        return formatDurationWords(input * 1000, true, true);
    }

    private static String formatDurationWords(long durationMillis, boolean suppressLeadingZero, boolean suppressTrailingZero) {

        LangFiles lang = LangFiles.getInstance();
        String days = lang.getLang().getString("Format.Plural.Days");
        String hours = lang.getLang().getString("Format.Plural.Hours");
        String minutes = lang.getLang().getString("Format.Plural.Minutes");
        String seconds = lang.getLang().getString("Format.Plural.Seconds");
        String day = lang.getLang().getString("Format.Singular.Day");
        String hour = lang.getLang().getString("Format.Singular.Hour");
        String minute = lang.getLang().getString("Format.Singular.Minute");
        String second = lang.getLang().getString("Format.Singular.Second");

        String duration = DurationFormatUtils.formatDuration(durationMillis, "d' " + days + " 'H' " + hours + " 'm' " + minutes + " 's' " + seconds + "'");
        String tmp;

        if (suppressLeadingZero) {
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
        }

        if (suppressTrailingZero) {
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
        }

        duration = " " + duration;
        duration = StringUtils.replaceOnce(duration, " 1 " + seconds, " 1 " + second);
        duration = StringUtils.replaceOnce(duration, " 1 " + minutes, " 1 " + minute);
        duration = StringUtils.replaceOnce(duration, " 1 " + hours, " 1 " + hour);
        duration = StringUtils.replaceOnce(duration, " 1 " + days, " 1 " + day);
        return duration.trim();
    }

}
