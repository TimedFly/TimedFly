package com.timedfly.Utilities;

import com.timedfly.ConfigurationFiles.ConfigCache;
import com.timedfly.ConfigurationFiles.ItemsConfig;
import com.timedfly.ConfigurationFiles.LangFiles;
import com.timedfly.Hooks.TokensManager;
import com.timedfly.Listeners.GUIListener;
import com.timedfly.Managers.TimeFormat;
import com.timedfly.TimedFly;
import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class FlyGUI {

    private ItemsConfig items = ItemsConfig.getInstance();
    private LangFiles lang = LangFiles.getInstance();
    private TokensManager tokensManager = new TokensManager();
    private Utility utility;
    private ConfigCache configCache;

    public FlyGUI(Utility utility, ConfigCache configCache) {
        this.utility = utility;
        this.configCache = configCache;
    }

    public void flyGui(Player player) {
        TimedFly plugin = TimedFly.getInstance();
        Economy economy = plugin.getEconomy();
        FileConfiguration itemscf = items.getItems();
        Inventory inv = Bukkit.createInventory(player, configCache.getGuiSlots(), Utility.color(configCache.getGuiDisplayName()));
        ConfigurationSection section = itemscf.getConfigurationSection("Items");

        for (String string : section.getKeys(false)) {
            String price = Integer.toString(itemscf.getInt("Items." + string + ".Price"));
            String time = Integer.toString(itemscf.getInt("Items." + string + ".Time"));
            List<String> lore = new ArrayList<>();
            List<String> l = itemscf.getStringList("Items." + string + ".Lore");
            ItemStack item = new ItemStack(Material.matchMaterial(itemscf.getString("Items." + string + ".Material")),
                    itemscf.getInt("Items." + string + ".Amount"), (short) itemscf.getInt("Items." + string + ".Data"));
            ItemMeta meta = item.getItemMeta();
            String pformat = NumberFormat.getIntegerInstance().format(Double.valueOf(price));
            if (!GUIListener.flytime.containsKey(player.getUniqueId())) {
                int i = (int) Math.round(economy.getBalance(player));
                String format = NumberFormat.getIntegerInstance().format(i);
                for (String list : l) {
                    if (utility.isPapiEnabled()) {
                        String loreNoPAPI = Utility.color(list).replace("%time%", time)
                                .replace("%price%", pformat).replace("%timeleft%", lang.getLang().getString("Format.NoTimeLeft"))
                                .replace("%balance%", format).replace("%tokens%", tokensManager.tokens(player));
                        String lorePAPI = PlaceholderAPI.setPlaceholders(player, loreNoPAPI);
                        lore.add(lorePAPI);
                    } else {
                        lore.add(Utility.color(list).replace("%time%", time)
                                .replace("%price%", pformat).replace("%timeleft%", lang.getLang().getString("Format.NoTimeLeft"))
                                .replace("%balance%", format).replace("%tokens%", tokensManager.tokens(player)));
                    }
                }
            } else {
                int i = (int) Math.round(economy.getBalance(player));
                String format = NumberFormat.getIntegerInstance().format(i);
                for (String list : l) {
                    if (utility.isPapiEnabled()) {
                        String loreNoPAPI = Utility.color(list).replace("%time%", time)
                                .replace("%price%", pformat).replace("%timeleft%", TimeFormat.format(GUIListener.flytime.get(player.getUniqueId())))
                                .replace("%balance%", format).replace("%tokens%", tokensManager.tokens(player));
                        String lorePAPI = PlaceholderAPI.setPlaceholders(player, loreNoPAPI);
                        lore.add(lorePAPI);
                    } else {
                        lore.add(Utility.color(list).replace("%time%", time)
                                .replace("%price%", pformat).replace("%timeleft%", TimeFormat.format(GUIListener.flytime.get(player.getUniqueId())))
                                .replace("%balance%", format).replace("%tokens%", tokensManager.tokens(player)));
                    }
                }
            }
            if (utility.isPapiEnabled()) {
                String nameNoPAPI = Utility.color(itemscf
                        .getString("Items." + string + ".Name").replace("%time%", time).replace("%price%", pformat));
                String namePAPI = PlaceholderAPI.setPlaceholders(player, nameNoPAPI);
                meta.setDisplayName(namePAPI);
            } else {
                meta.setDisplayName(Utility.color(itemscf
                        .getString("Items." + string + ".Name").replace("%time%", time).replace("%price%", pformat)));
            }
            meta.setLore(lore);

            if (itemscf.getBoolean("Items." + string + ".Hide_Attributes")) {
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            }
            if (itemscf.getBoolean("Items." + string + ".Hide_Enchants")) {
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            if (itemscf.getBoolean("Items." + string + ".Hide_Place_On")) {
                meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
            }
            if (itemscf.getBoolean("Items." + string + ".Hide_Potion_Effects")) {
                meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            }
            if (itemscf.getBoolean("Items." + string + ".Hide_Unbreakable")) {
                meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            }
            if (itemscf.getBoolean("Items." + string + ".Glow")) {
                meta.addEnchant(Enchantment.DURABILITY, 1, true);
            }
            item.setItemMeta(meta);
            inv.setItem(itemscf.getInt("Items." + string + ".Slot"), item);
        }
        player.openInventory(inv);
    }
}
