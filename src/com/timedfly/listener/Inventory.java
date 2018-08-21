package com.timedfly.listener;

import com.timedfly.NMS.NMS;
import com.timedfly.configurations.ConfigCache;
import com.timedfly.configurations.ItemsConfig;
import com.timedfly.configurations.Languages;
import com.timedfly.managers.CooldownManager;
import com.timedfly.managers.CurrencyManager;
import com.timedfly.managers.PlayerManager;
import com.timedfly.utilities.Message;
import com.timedfly.utilities.TimeFormat;
import com.timedfly.utilities.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class Inventory implements Listener {

    private CurrencyManager currencyManager;
    private ItemsConfig itemsConfig;
    private Languages languages;
    private Utilities utility;
    private NMS nms;

    public Inventory(CurrencyManager currencyManager, Utilities utility, Languages languages, ItemsConfig itemsConfig, NMS nms) {
        this.currencyManager = currencyManager;
        this.utility = utility;
        this.languages = languages;
        this.itemsConfig = itemsConfig;
        this.nms = nms;
    }

    @EventHandler
    public void inventoryClickEvent(InventoryClickEvent event) {
        if (!ConfigCache.isGuiEnable()) return;
        Player player = (Player) event.getWhoClicked();

        if (!utility.isWorldEnabled(player.getWorld())) return;

        if (event.getView().getTopInventory().getTitle().equals(Message.color(ConfigCache.getGuiDisplayName()))) {
            String cooldowntime = ConfigCache.getCooldown();

            int slot = event.getSlot();
            FileConfiguration languageConfig = languages.getLanguageFile();
            FileConfiguration itemsConfig = this.itemsConfig.getItemsConfig();
            PlayerManager playerCache = utility.getPlayerManager(player.getUniqueId());

            event.setCancelled(true);
            ConfigurationSection section = itemsConfig.getConfigurationSection("Items");
            for (String string : section.getKeys(false)) {
                if (slot == itemsConfig.getInt("Items." + string + ".Slot") && event.getCurrentItem().hasItemMeta()) {
                    if (ConfigCache.isSkipFlightTimeIfHasPerm() && player.hasPermission("timedfly.fly.onoff")) {
                        Message.sendMessage(player, Message.color(languageConfig.getString("Other.CannotDo")));
                        player.closeInventory();
                        return;
                    }
                    if (itemsConfig.getBoolean("Items." + string + ".UsePermission") && !player.hasPermission(itemsConfig.getString("Items." + string + ".Permission"))) {
                        player.closeInventory();
                        Message.sendMessage(player, Message.color(itemsConfig.getString("Items." + string + ".PermissionMSG")));
                        return;
                    }

                    int price = itemsConfig.getInt("Items." + string + ".Price");
                    int time = itemsConfig.getInt("Items." + string + ".Time");

                    if (!player.hasPermission("timedfly.limit.bypass") && time > ConfigCache.getLimitMaxTime()) {
                        Message.sendMessage(player, languageConfig.getString("Other.MaxAllowed")
                                .replace("%time_allowed%", ConfigCache.getLimitMaxTime() + Languages.getFormat("Format.Plural.Minutes")));
                        player.closeInventory();
                        return;
                    }

                    if (startCooldown(player, cooldowntime)) {
                        Message.sendMessage(player, languageConfig.getString("Other.OnCooldown")
                                .replace("%cooldown%", TimeFormat.formatLong(CooldownManager.getTimeLeft(player.getUniqueId(), "fly"))));
                        player.closeInventory();
                        return;
                    }

                    if (!currencyManager.noCurrencyFound(player, languageConfig)) return;
                    if (!currencyManager.withdraw(player, price, time)) return;

                    playerCache.addTime(time * 60);
                    utility.runCommands(player, itemsConfig.getStringList("Items." + string + ".OnClick"));

                    Message.sendMessage(player, languageConfig.getString("Fly.Message.Enabled").replace("%price%",
                            "" + price).replace("%time%", "" + time));

                    if (ConfigCache.isLogConsoleOnBuy())
                        Message.sendMessage(Bukkit.getConsoleSender(), languageConfig.getString("Fly.Message.ConsoleBuyLog").replace("%price%",
                                "" + price).replace("%time%", "" + time).replace("%player%", player.getDisplayName()));

                    if (ConfigCache.isMessagesTitle()) {
                        nms.sendTitle(player, Message.color(languageConfig.getString("Fly.Titles.Enabled.Title")
                                .replace("%time%", TimeFormat.formatLong(time * 60))), 20, 40, 20);
                        nms.sendSubtitle(player, Message.color(languageConfig.getString("Fly.Titles.Enabled.SubTitle")
                                .replace("%time%", TimeFormat.formatLong(time * 60))), 20, 40, 20);
                    }
                }
            }
        }
    }

    private boolean startCooldown(Player player, String cooldownTime) {
        if (!player.hasPermission("timedfly.cooldown.bypass") || !player.isOp()) {
            if (!CooldownManager.isInCooldown(player.getUniqueId(), "fly")) {
                CooldownManager cooldownManager = new CooldownManager(player.getUniqueId(),
                        "fly", TimeFormat.timeToSeconds(cooldownTime).intValue());
                cooldownManager.start();
            } else return true;
        }
        return false;
    }
}
