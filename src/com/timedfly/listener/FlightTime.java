package com.timedfly.listener;

import com.timedfly.NMS.NMS;
import com.timedfly.configurations.ConfigCache;
import com.timedfly.configurations.Languages;
import com.timedfly.customevents.FlightTimeEndEvent;
import com.timedfly.customevents.FlightTimeStartEvent;
import com.timedfly.customevents.FlightTimeSubtractEvent;
import com.timedfly.managers.BossBarManager;
import com.timedfly.utilities.FlyGUI;
import com.timedfly.utilities.Message;
import com.timedfly.utilities.TimeFormat;
import com.timedfly.utilities.Utilities;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class FlightTime implements Listener {

    private NMS nms;
    private FlyGUI flyGUI;
    private Utilities utility;
    private FileConfiguration languageConfig;

    public FlightTime(NMS nms, FlyGUI flyGUI, Utilities utility, Languages languages) {
        this.nms = nms;
        this.flyGUI = flyGUI;
        this.utility = utility;
        this.languageConfig = languages.getLanguageFile();
    }

    @EventHandler
    public void flightTimeStartEvent(FlightTimeStartEvent event) {

    }

    @EventHandler
    public void flightTimeSubtractEvent(FlightTimeSubtractEvent event) {
        if (!utility.isWorldEnabled(event.getPlayer().getWorld())) return;
        Player player = event.getPlayer();
        int timeLeft = event.getTimeLeft();
        BossBarManager bossBarManager = event.getPlayerManager().getBossBarManager();

        if (ConfigCache.isMessagesActionBar())
            nms.sendActionbar(player, Message.color(languageConfig.getString("Fly.ActionBar")
                    .replace("%timeleft%", TimeFormat.formatLong(event.getTimeLeft()))));

        if (ConfigCache.isBossBarTimerEnabled()) bossBarManager.setBarName(Languages.getFormat("Fly.BossBar")
                .replace("%timeleft%", TimeFormat.formatLong(timeLeft))).setBarProgress(timeLeft);

        List<String> announce = ConfigCache.getAnnouncerTitleTimes();
        for (String list : announce) {
            if (timeLeft != Integer.parseInt(list)) continue;

            if (ConfigCache.isSoundsEnabled()) {
                player.playSound(player.getLocation(), Sound.valueOf(ConfigCache.getSoundsAnnouncer()), 2, 1);
            }
            if (ConfigCache.isAnnouncerChatEnabled()) {
                Message.sendMessage(player, languageConfig.getString("Announcer.Chat.Message").replace(
                        "%time%", TimeFormat.formatLong(timeLeft)));
            }
            if (ConfigCache.isMessagesTitle() || ConfigCache.isAnnouncerTitleEnabled()) {
                nms.sendTitle(player, Message.color(languageConfig.getString("Announcer.Titles.Title")
                        .replace("%time%", TimeFormat.formatLong(timeLeft))), 0, 30, 0);
                nms.sendSubtitle(player, Message.color(languageConfig.getString("Announcer.Titles.SubTitle")
                        .replace("%time%", TimeFormat.formatLong(timeLeft))), 0, 30, 0);
            }
        }

        if (player.getOpenInventory().getTopInventory().equals(flyGUI.getInventory())) {
            flyGUI.openGui(player);
        }
    }

    @EventHandler
    public void flightTimeEndEvent(FlightTimeEndEvent event) {
        if (!utility.isWorldEnabled(event.getPlayer().getWorld())) return;
        Player player = event.getPlayer();

        if (event.isTimePaused()) return;

        if (ConfigCache.isSoundsEnabled())
            player.playSound(player.getLocation(), Sound.valueOf(ConfigCache.getSoundsFlightDisabled()), 100, 1);

        Message.sendMessage(player, languageConfig.getString("Fly.Message.Disabled"));
        if (ConfigCache.isMessagesTitle()) {
            nms.sendTitle(player, Message.color(languageConfig.getString("Fly.Titles.Disabled.Title")
                    .replace("%time%", TimeFormat.formatLong(event.getTimeLeft()))), 0, 40, 20);
            nms.sendSubtitle(player, Message.color(languageConfig.getString("Fly.Titles.Disabled.SubTitle")
                    .replace("%time%", TimeFormat.formatLong(event.getTimeLeft()))), 0, 40, 20);
        }

        if (ConfigCache.isOnFlyDisableCommandsEnabled())
            utility.runCommands(player, ConfigCache.getOnFlyDisableCommands());
    }
}
