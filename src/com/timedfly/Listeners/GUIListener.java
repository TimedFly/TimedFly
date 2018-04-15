package com.timedfly.Listeners;

import com.timedfly.ConfigurationFiles.ConfigCache;
import com.timedfly.ConfigurationFiles.ItemsConfig;
import com.timedfly.ConfigurationFiles.LangFiles;
import com.timedfly.Managers.CooldownManager;
import com.timedfly.Managers.RemoveMoney;
import com.timedfly.Managers.TimeFormat;
import com.timedfly.TimedFly;
import com.timedfly.Utilities.FlyGUI;
import com.timedfly.Utilities.PlayerCache;
import com.timedfly.Utilities.Utility;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class GUIListener implements Listener {

    public static HashMap<UUID, Integer> flytime = new HashMap<>();
    public static Set<UUID> after = new HashSet<>();
    private TimedFly plugin;
    private TimeFormat format;
    private LangFiles lang = LangFiles.getInstance();
    private ItemsConfig items = ItemsConfig.getInstance();
    private Utility utility;
    private RemoveMoney removeMoney;
    private ConfigCache configCache;

    public GUIListener(TimedFly plugin, Utility utility, RemoveMoney removeMoney, ConfigCache configCache) {
        this.plugin = plugin;
        this.utility = utility;
        this.removeMoney = removeMoney;
        this.format = new TimeFormat(plugin);
        this.configCache = configCache;

        FileConfiguration config = lang.getLang();
        FlyGUI gui = new FlyGUI(utility, configCache);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (flytime.isEmpty()) {
                    return;
                }
                for (Map.Entry<UUID, Integer> entry : flytime.entrySet()) {

                    Player player = Bukkit.getPlayer(entry.getKey());
                    PlayerCache playerCache = utility.getPlayerCache(player);
                    Integer time = entry.getValue();

                    if (time == 0) {
                        flytime.remove(entry.getKey());

                        playerCache.setInitialTime(0);
                        playerCache.setTimeLeft(0);
                        if (configCache.isBossBarTimerEnabled()) playerCache.getBossBarManager().hide();

                        if (player != null && utility.isWorldEnabled(player.getWorld())) {
                            after.add(player.getUniqueId());
                            Bukkit.getScheduler().runTaskLater(plugin, () -> after.remove(player.getUniqueId()), 6 * 20L);

                            playerCache.setFlying(false);

                            if (configCache.isSoundsEnabled())
                                player.playSound(player.getLocation(), Sound.valueOf(configCache.getSoundsFlightDisabled()), 100, 1);

                            utility.message(player, Utility.color(config.getString("Fly.Message.Disabled")));
                            if (configCache.isMessagesTitle()) {
                                plugin.getNMS().sendTitle(player, Utility.color(config.getString("Fly.Titles.Disabled.Title").replace(
                                        "%time%", TimeFormat.format(time - 1))), 0, 40, 20);
                                plugin.getNMS().sendSubtitle(player, Utility.color(config.getString("Fly.Titles.Disabled.SubTitle").replace(
                                        "%time%", TimeFormat.format(time - 1))), 0, 40, 20);
                            }
                        }
                        if (configCache.isOnFlyDisableCommandsEnabled()) {
                            utility.clickEvent(player, configCache.getOnFlyDisableCommands());
                        }
                    } else if (time >= 1) {
                        flytime.put(entry.getKey(), time - 1);
                        if (player != null && utility.isWorldEnabled(player.getWorld())) {
                            if (player.getOpenInventory().getTitle().equals(Utility.color(configCache.getGuiDisplayName()))) {
                                gui.flyGui(player);
                            }

                            playerCache.setTimeLeft(time - 1);
                            if (configCache.isMessagesActionBar()) format.setActionBar(player, config, time);
                            if (configCache.isBossBarTimerEnabled())
                                playerCache.getBossBarManager().setInitialTime(playerCache.getInitialTime())
                                        .setBarProgress(playerCache.getTimeLeft())
                                        .setTitle(Utility.color(LangFiles.getInstance().getLang().getString("Fly.BossBar")
                                                .replace("%timeleft%", TimeFormat.format(playerCache.getTimeLeft()))));

                            List<String> announce = configCache.getAnnouncerTitleTimes();
                            for (String list : announce) {
                                if (time - 1 == Integer.parseInt(list)) {
                                    if (configCache.isSoundsEnabled()) {
                                        player.playSound(player.getLocation(), Sound.valueOf(configCache.getSoundsAnnouncer()), 2, 1);
                                    }
                                    if (configCache.isMessagesActionBar()) {
                                        utility.message(player, Utility.color(config.getString("Announcer.Chat.Message").replace(
                                                "%time%", TimeFormat.format(time - 1))));
                                    }
                                    if (configCache.isMessagesTitle()) {
                                        plugin.getNMS().sendTitle(player, Utility.color(config.getString("Announcer.Titles.Title")
                                                .replace("%time%", TimeFormat.format(time - 1))), 0, 30, 0);
                                        plugin.getNMS().sendSubtitle(player, Utility.color(config.getString("Announcer.Titles.SubTitle")
                                                .replace("%time%", TimeFormat.format(time - 1))), 0, 30, 0);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    @EventHandler
    public void flyListenerGui(InventoryClickEvent event) {
        String cooldowntime = configCache.getCooldown();

        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        FileConfiguration config = lang.getLang();
        FileConfiguration itemscf = items.getItems();
        PlayerCache playerCache = utility.getPlayerCache(player);

        if (utility.isWorldEnabled(player.getWorld())) {
            if (event.getView().getTopInventory().getTitle().equals(Utility.color(configCache.getGuiDisplayName()))) {
                event.setCancelled(true);
                ConfigurationSection section = itemscf.getConfigurationSection("Items");
                for (String string : section.getKeys(false)) {
                    if (slot == itemscf.getInt("Items." + string + ".Slot") && event.getCurrentItem().hasItemMeta()) {
                        if (itemscf.getBoolean("Items." + string + ".UsePermission") && !player.hasPermission(itemscf.getString("Items." + string + ".Permission"))) {
                            player.closeInventory();
                            utility.message(player, Utility.color(
                                    itemscf.getString("Items." + string + ".PermissionMSG")));
                            return;
                        }
                        if (itemscf.getBoolean("Items." + string + ".FlyItem")) {
                            int price = itemscf.getInt("Items." + string + ".Price");
                            int time = itemscf.getInt("Items." + string + ".Time");

                            if (removeMoney.noCurrencyFound(player, config)) return;
                            if (removeMoney.withdraw(player, price, time)) return;

                            playerCache.setInitialTime(time * 60);

                            if (startCooldown(player, cooldowntime)) {
                                utility.message(player, config.getString("Other.OnCooldown").replace("%cooldown%",
                                        TimeFormat.format(CooldownManager.getTimeLeft(player.getUniqueId(), "fly"))));
                                player.closeInventory();
                                return;
                            }

                            if (!flytime.containsKey(player.getUniqueId())) {
                                if (!player.hasPermission("timedfly.limit.bypass") && time > configCache.getLimitMaxTime()) {
                                    utility.message(player, config.getString("Other.MaxAllowed"));
                                    return;
                                }

                                flytime.put(player.getUniqueId(), time * 60);
                            } else {
                                flytime.put(player.getUniqueId(), flytime.get(player.getUniqueId()) + time * 60);
                            }

                            playerCache.setFlying(true);
                            if (configCache.isBossBarTimerEnabled())
                                playerCache.getBossBarManager().setInitialTime(playerCache.getInitialTime())
                                        .setCurrentTime(playerCache.getTimeLeft()).show();

                            utility.message(player, config.getString("Fly.Message.Enabled").replace("%price%",
                                    "" + price).replace("%time%", "" + time));

                            if (configCache.isMessagesTitle()) {
                                plugin.getNMS().sendTitle(player, Utility.color(config.getString("Fly.Titles.Enabled.Title")
                                        .replace("%time%", TimeFormat.format(time * 60))), 20, 40, 20);
                                plugin.getNMS().sendSubtitle(player, Utility.color(config.getString("Fly.Titles.Enabled.SubTitle")
                                        .replace("%time%", TimeFormat.format(time * 60))), 20, 40, 20);
                            }
                            utility.clickEvent(player, itemscf.getStringList("Items." + string + ".OnClick"));
                        } else {
                            utility.clickEvent(player, itemscf.getStringList("Items." + string + ".OnClick"));
                        }
                    }
                }
            }
        }
    }

    private boolean startCooldown(Player player, String cooldownTime) {
        if (!player.hasPermission("timedfly.cooldown.bypass") || !player.isOp()) {
            if (!CooldownManager.isInCooldown(player.getUniqueId(), "fly")) {
                CooldownManager cooldownManager = new CooldownManager(player.getUniqueId(),
                        "fly", utility.timeToSeconds(cooldownTime).intValue());
                cooldownManager.start();
            } else return true;
        }
        return false;
    }
}
