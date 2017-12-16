package com.minestom.Utilities.GUI;

import com.minestom.ConfigurationFiles.ItemsConfig;
import com.minestom.ConfigurationFiles.LangFiles;
import com.minestom.Managers.CooldownManager;
import com.minestom.Managers.DependenciesManager;
import com.minestom.Managers.MessageManager;
import com.minestom.Managers.TimeFormat;
import com.minestom.TimedFly;
import com.minestom.Utilities.Others.OnClick;
import com.minestom.Utilities.Utility;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GUIListener implements Listener {

    private TimedFly plugin = TimedFly.getInstance();
    private TimeFormat format = new TimeFormat();
    public static HashMap<UUID, Integer> flytime = new HashMap<>();
    public static HashMap<UUID, Integer> godmode = new HashMap<>();
    private LangFiles lang = LangFiles.getInstance();
    private ItemsConfig items = ItemsConfig.getInstance();
    private Utility utility = new Utility(plugin);

    public GUIListener() {
        FileConfiguration config = lang.getLang();
        FlyGUI gui = new FlyGUI();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<UUID, Integer> entry : flytime.entrySet()) {
                    Player player = Bukkit.getPlayer(entry.getKey());
                    Integer time = entry.getValue();
                    if (time == 0) {
                        if (player == null) {
                            flytime.remove(entry.getKey());
                        } else {
                            flytime.remove(player.getUniqueId());
                            if (utility.isWorldEnabled(player)) {
                                godmode.put(player.getUniqueId(), 6);
                                player.setAllowFlight(false);
                                player.setFlying(false);
                                player.playSound(player.getLocation(), Sound.valueOf(config.getString("Fly.DisabledSound")), 100, 1);
                                utility.message(player, utility.color(config.getString("Fly.Message.Disabled")));
                                if (config.getBoolean("Fly.Titles.Disabled.Use")) {
                                    plugin.getNMS().sendTitle(player, utility.color(config.getString("Fly.Titles.Disabled.Title").replace(
                                            "%time%", format.format(time - 1))), 0, 40, 20);
                                    plugin.getNMS().sendSubtitle(player, utility.color(config.getString("Fly.Titles.Disabled.SubTitle").replace(
                                            "%time%", format.format(time - 1))), 0, 40, 20);
                                }
                            }
                        }
                    } else {
                        flytime.put(entry.getKey(), time - 1);
                        if (player != null) {
                            if (utility.isWorldEnabled(player)) {
                                if (player.getOpenInventory().getTitle().equals(utility.color(plugin.getConfig().getString("Gui.DisplayName")))) {
                                    gui.flyGui(player);
                                }
                                format.setActionBar(player, config);
                                List<String> announce = config.getStringList("Announcer.Times");
                                for (String list : announce) {
                                    if (time - 1 == Integer.parseInt(list)) {
                                        player.playSound(player.getLocation(), Sound.valueOf(config.getString("Announcer.Sound")), 2, 1);
                                        if (config.getBoolean("Announcer.Chat.Enabled")) {
                                            utility.message(player, utility.color(config.getString("Announcer.Chat.Message").replace(
                                                    "%time%", format.format(time - 1))));
                                        }
                                        if (config.getBoolean("Announcer.Titles.Enabled")) {
                                            plugin.getNMS().sendTitle(player, utility.color(config.getString("Announcer.Titles.Title")
                                                    .replace("%time%", format.format(time - 1))), 0, 30, 0);
                                            plugin.getNMS().sendSubtitle(player, utility.color(config.getString("Announcer.Titles.SubTitle")
                                                    .replace("%time%", format.format(time - 1))), 0, 30, 0);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);

    }

    private String cooldowntime = plugin.getConfig().getString("Cooldown");
    private int t = Integer.parseInt(cooldowntime.replaceAll("[a-zA-Z]", ""));
    private static DependenciesManager depends = new DependenciesManager(TimedFly.getInstance());

    @EventHandler
    public void flyListenerGui(InventoryClickEvent event) {
        Economy economy = plugin.getEconomy();
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        FileConfiguration config = lang.getLang();
        FileConfiguration itemscf = items.getItems();
        if (utility.isWorldEnabled(player)) {
            if (event.getView().getTopInventory().getTitle().equals(utility.color(plugin.getConfig().getString("Gui.DisplayName")))) {
                event.setCancelled(true);
                ConfigurationSection section = itemscf.getConfigurationSection("Items");
                for (String string : section.getKeys(false)) {
                    if (slot == itemscf.getInt("Items." + string + ".Slot") && event.getCurrentItem().hasItemMeta()) {
                        if (itemscf.getBoolean("Items." + string + ".UsePermission") && !player.hasPermission(itemscf.getString("Items." + string + ".Permission"))) {
                            player.closeInventory();
                            utility.message(player, utility.color(
                                    itemscf.getString("Items." + string + ".PermissionMSG")));
                            return;
                        }
                        if (itemscf.getBoolean("Items." + string + ".FlyItem")) {
                            int price = itemscf.getInt("Items." + string + ".Price");
                            int time = itemscf.getInt("Items." + string + ".Time");
                            if (!flytime.containsKey(player.getUniqueId())) {
                                if (!player.hasPermission("timedfly.limit.bypass") && time > plugin.getConfig().getInt("LimitMaxTime")) {
                                    utility.message(player, config.getString("Other.MaxAllowed"));
                                    return;
                                }
                                if (plugin.getConfig().getBoolean("UseTokenManager")) {
                                    if (depends.getTokens(player) >= price) {
                                        depends.removeTokens(player, price);
                                    } else {
                                        player.closeInventory();
                                        utility.message(player, MessageManager.NOMONEY.toString().replace("%price%", Integer.toString(price))
                                                .replace("%time%", Integer.toString(time)));
                                        return;
                                    }
                                }
                                if (plugin.getConfig().getBoolean("UseVault")) {
                                    if (economy.has(player, price)) {
                                        economy.withdrawPlayer(player, price);
                                    } else {
                                        player.closeInventory();
                                        utility.message(player, MessageManager.NOMONEY.toString().replace("%price%", Integer.toString(price))
                                                .replace("%time%", Integer.toString(time)));
                                        return;
                                    }
                                }
                                if (!player.getAllowFlight()) {
                                    player.setAllowFlight(true);
                                }
                                flytime.put(player.getUniqueId(), time * 60);
                                if (!player.hasPermission("timedfly.cooldown.bypass") || !player.isOp()) {
                                    if (!CooldownManager.isInCooldown(player.getUniqueId(), "fly")) {
                                        if (cooldowntime.contains("s")) {
                                            CooldownManager cooldownManager = new CooldownManager(player.getUniqueId(), "fly", t);
                                            cooldownManager.start();
                                        }
                                        if (cooldowntime.contains("m")) {
                                            t = t * 60;
                                            CooldownManager cooldownManager = new CooldownManager(player.getUniqueId(), "fly", t);
                                            cooldownManager.start();
                                        }
                                        if (cooldowntime.contains("h")) {
                                            t = t * 3600;
                                            CooldownManager cooldownManager = new CooldownManager(player.getUniqueId(), "fly", t);
                                            cooldownManager.start();
                                        }
                                        if (cooldowntime.contains("d")) {
                                            t = t * 86400;
                                            CooldownManager cooldownManager = new CooldownManager(player.getUniqueId(), "fly", t);
                                            cooldownManager.start();
                                        }
                                    }
                                }
                            } else {
                                if (plugin.getConfig().getBoolean("UseTokenManager")) {
                                    if (depends.getTokens(player) >= price) {
                                        depends.removeTokens(player, price);
                                    } else {
                                        player.closeInventory();
                                        utility.message(player, MessageManager.NOMONEY.toString().replace("%price%", Integer.toString(price))
                                                .replace("%time%", Integer.toString(time)));
                                        return;
                                    }
                                }
                                if (plugin.getConfig().getBoolean("UseVault")) {
                                    if (economy.has(player, price)) {
                                        economy.withdrawPlayer(player, price);
                                    } else {
                                        player.closeInventory();
                                        utility.message(player, MessageManager.NOMONEY.toString().replace("%price%", Integer.toString(price))
                                                .replace("%time%", Integer.toString(time)));
                                        return;
                                    }
                                }
                                flytime.put(player.getUniqueId(), flytime.get(player.getUniqueId()) + time * 60);
                                if (!player.hasPermission("timedfly.cooldown.bypass") || !player.isOp()) {
                                    if (!CooldownManager.isInCooldown(player.getUniqueId(), "fly")) {
                                        if (cooldowntime.contains("s")) {
                                            CooldownManager cooldownManager = new CooldownManager(player.getUniqueId(), "fly", t);
                                            cooldownManager.start();
                                        }
                                        if (cooldowntime.contains("m")) {
                                            t = t * 60;
                                            CooldownManager cooldownManager = new CooldownManager(player.getUniqueId(), "fly", t);
                                            cooldownManager.start();
                                        }
                                        if (cooldowntime.contains("h")) {
                                            t = t * 3600;
                                            CooldownManager cooldownManager = new CooldownManager(player.getUniqueId(), "fly", t);
                                            cooldownManager.start();
                                        }
                                        if (cooldowntime.contains("d")) {
                                            t = t * 86400;
                                            CooldownManager cooldownManager = new CooldownManager(player.getUniqueId(), "fly", t);
                                            cooldownManager.start();
                                        }
                                    }
                                }
                            }
                            utility.message(player, config.getString("Fly.Message.Enabled").replace("%price%",
                                    "" + price).replace("%time%", "" + time));
                            if (config.getBoolean("Fly.Titles.Enabled.Use")) {
                                plugin.getNMS().sendTitle(player, utility.color(config.getString("Fly.Titles.Enabled.Title")
                                        .replace("%time%", format.format(time * 60))), 20, 40, 20);
                                plugin.getNMS().sendSubtitle(player, utility.color(config.getString("Fly.Titles.Enabled.SubTitle")
                                        .replace("%time%", format.format(time * 60))), 20, 40, 20);
                            }
                            OnClick.clickEvent(player, string);
                        } else {
                            OnClick.clickEvent(player, string);
                        }
                    }
                }
            }
        }
    }
}
