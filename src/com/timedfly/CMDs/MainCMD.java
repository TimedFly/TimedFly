package com.timedfly.CMDs;

import com.timedfly.ConfigurationFiles.ConfigCache;
import com.timedfly.ConfigurationFiles.ItemsConfig;
import com.timedfly.ConfigurationFiles.LangFiles;
import com.timedfly.ConfigurationFiles.UpdateConfig;
import com.timedfly.TimedFly;
import com.timedfly.Utilities.Utility;
import mkremins.fanciful.FancyMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class MainCMD implements CommandExecutor {

    public TimedFly plugin;
    private LangFiles lang = LangFiles.getInstance();
    private ItemsConfig items = ItemsConfig.getInstance();
    private Utility utility;
    private UpdateConfig updateConfig = new UpdateConfig();
    private ConfigCache configCache;

    public MainCMD(TimedFly plugin, ConfigCache configCache) {
        this.plugin = plugin;
        this.utility = plugin.getUtility();
        this.configCache = configCache;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args) {
        if (cmd.getName().equalsIgnoreCase("timedfly")) {
            FileConfiguration config = lang.getLang();
            FileConfiguration itemscf = items.getItems();
            if (args.length == 0) {
                sender.sendMessage(Utility.color("&8&m----------------------------------------"));
                sender.sendMessage(Utility.color("&c&lTimedFly &7created by &cBy_Jack"));
                new FancyMessage(Utility.color("&7To see all commands available use "))
                        .then(Utility.color("&c/tf help"))
                        .tooltip(Utility.color("&aClick here to get help"))
                        .command("/timedfly help").send(sender);
                sender.sendMessage(Utility.color("&8&m----------------------------------------"));
                return true;
            }
            if (args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(Utility.color("&8&m----------------------------------------"));
                sender.sendMessage(Utility.color("&c&l                   TimedFly"));
                sender.sendMessage("");
                new FancyMessage(Utility.color("&6» &a/tf help &7- show this help page"))
                        .tooltip("§aClick to execute /tf help").command("/tf help").send(sender);
                new FancyMessage(Utility.color("&6» &a/tf reload &7- reloads the config file"))
                        .tooltip("§aClick to execute /tf reload").command("/tf reload").send(sender);
                new FancyMessage(Utility.color("&6» &a/tf setTime <itemID> <minutes> &7- create a new timed fly"))
                        .tooltip("§aClick to execute /tf setTime <itemID> <minutes>")
                        .suggest("/tf setTime <itemID> <minutes>")
                        .send(sender);
                new FancyMessage(Utility.color("&6» &a/tf setPrice <itemID> <price> &7- create a new timed fly"))
                        .tooltip("§aClick to execute /tf setPrice <itemID> <price>")
                        .suggest("/tf setPrice <itemID> <price>")
                        .send(sender);
                new FancyMessage(Utility.color("&6» &a/tf setItem <itemID> &7- set the item that you have on hand"))
                        .tooltip("§aClick to execute /tf setItem <itemID>")
                        .suggest("/tf setItem <itemID>")
                        .send(sender);
                new FancyMessage(Utility.color("&6» &a/tf permissions &7- see all available permissions"))
                        .tooltip("§aClick to execute /tf permissions").command("/tf permissions")
                        .send(sender);
                new FancyMessage(Utility.color("&6» &a/tf list &7- see all the ItemID"))
                        .tooltip("§aClick to execute /tf list").command("/tf list").send(sender);
                sender.sendMessage("");
                sender.sendMessage(Utility.color("&7Pro Tip: You can hover over the commands and click them"));
                sender.sendMessage("");
                new FancyMessage(Utility.color("                 §7<<<         "))
                        .then("§e>>>").tooltip("§aNext Page").command("/tf help2").send(sender);
                sender.sendMessage(Utility.color(""));
                sender.sendMessage(Utility.color("&7                 Version: &c" + plugin.getDescription().getVersion()));
                sender.sendMessage(Utility.color("&8&m----------------------------------------"));
                return true;
            }
            if (args[0].equalsIgnoreCase("help2")) {
                sender.sendMessage(Utility.color("&8&m----------------------------------------"));
                sender.sendMessage(Utility.color("&c&l                   TimedFly"));
                sender.sendMessage(Utility.color(""));
                new FancyMessage(Utility.color("&6» &a/tf help 2 &7- show this help page"))
                        .tooltip("§aClick to execute /tf help 2").command("/tf help2").send(sender);
                new FancyMessage(Utility.color(
                        "&6» &a/tfly &7- opens the fly menu"))
                        .tooltip("§aClick to execute /tfly")
                        .command("/tfly").send(sender);
                new FancyMessage(Utility.color(
                        "&6» &a/tfly set <player> <minutes> &7- set fly mode to another &7player, no cost"))
                        .tooltip("§aClick to insert /tfly set <player> <minutes>")
                        .suggest("/tfly set <player> <minutes>")
                        .send(sender);
                new FancyMessage(Utility.color(
                        "&6» &a/tfly add <player> <minutes> &7- add minutes to another &7player, no cost"))
                        .tooltip("§aClick to insert /tfly add <player> <minutes>")
                        .suggest("/tfly add <player> <minutes>")
                        .send(sender);
                new FancyMessage(Utility.color(
                        "&6» &a/tfly on &7- set fly mode to yourself, no time nor cost"))
                        .tooltip("§aClick to execute /tfly on")
                        .command("/tfly on")
                        .send(sender);
                new FancyMessage(Utility.color(
                        "&6» &a/tfly off &7- unset fly mode to yourself"))
                        .tooltip("§aClick to execute /tfly off")
                        .command("/tfly off")
                        .send(sender);
                new FancyMessage(Utility.color(
                        "&6» &a/tfly timeleft &7[player] - check how much time do you &7have &7left"))
                        .tooltip("§aClick to execute /tfly timeleft")
                        .command("/tfly timeleft")
                        .send(sender);
                sender.sendMessage(Utility.color(""));
                sender.sendMessage(Utility.color("&7Pro Tip: You can hover over the commands and click them"));
                sender.sendMessage(Utility.color(""));
                new FancyMessage("                 ").then("§e<<<")
                        .tooltip("§aPrevious Page").command("/tf help").then("         §e>>>").tooltip("§aNext Page").command("/tf help3").send(sender);
                sender.sendMessage(Utility.color(""));
                sender.sendMessage(Utility.color("&7                 Version: &c" + plugin.getDescription().getVersion()));
                sender.sendMessage(Utility.color("&8&m----------------------------------------"));
                return true;
            }
            if (args[0].equalsIgnoreCase("help3")) {
                sender.sendMessage(Utility.color("&8&m----------------------------------------"));
                sender.sendMessage(Utility.color("&c&l                   TimedFly"));
                sender.sendMessage(Utility.color(""));
                new FancyMessage(Utility.color(
                        "&6» &a/tfly hide &7- hide and save your time left"))
                        .tooltip("§aClick to execute /tfly hide")
                        .command("/tfly hide").send(sender);
                new FancyMessage(Utility.color(
                        "&6» &a/tfly resume &7- resume your time left"))
                        .tooltip("§aClick to execute /tfly resume")
                        .command("/tfly resume").send(sender);
                sender.sendMessage(Utility.color(""));
                sender.sendMessage(Utility.color("&7Pro Tip: You can hover over the commands and click them"));
                sender.sendMessage(Utility.color(""));
                new FancyMessage("                 ").then("§e<<<")
                        .tooltip("§aPrevious Page").command("/tf help2").then("         §7>>>").send(sender);
                sender.sendMessage(Utility.color(""));
                sender.sendMessage(Utility.color("&7                 Version: &c" + plugin.getDescription().getVersion()));
                sender.sendMessage(Utility.color("&8&m----------------------------------------"));
            }
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("timedfly.admin")) {
                    reloadFiles();
                    utility.message(sender, "&econfig.yml was succesfully reloaded.");
                    utility.message(sender, "&eitems.yml was succesfully reloaded.");
                    utility.message(sender, "&elang_" + configCache.getLanguge() + ".yml was succesfully reloaded.");
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        plugin.getNMS().sendTitle(player, Utility.color("All files reloaded"), 20, 40, 20);
                    }
                    return true;
                } else {
                    utility.message(sender, config.getString("Other.NoPermission.Message"));
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        plugin.getNMS().sendTitle(player, Utility.color(config.getString("Other.NoPermission.Title")), 20, 40, 20);
                        plugin.getNMS().sendTitle(player, Utility.color(config.getString("Other.NoPermission.SubTitle")), 20, 40, 20);
                    }
                    return true;
                }
            }
            if (args[0].equalsIgnoreCase("list")) {
                if (sender.hasPermission("timedfly.admin")) {
                    ConfigurationSection section = itemscf.getConfigurationSection("Items");
                    int i = 0;
                    sender.sendMessage(Utility.color("&8&m----------------------------------------"));
                    sender.sendMessage(
                            Utility.color("&c         TimedFly ItemID List"));
                    sender.sendMessage(Utility.color(""));
                    for (String list : section.getKeys(false)) {
                        i++;
                        sender.sendMessage(Utility.color("&e" + i + ": &7" + list));
                    }
                    sender.sendMessage(Utility.color("&8&m----------------------------------------"));
                    return true;
                } else {
                    utility.message(sender, config.getString("Other.NoPermission.Message"));
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        plugin.getNMS().sendTitle(player, Utility.color(config.getString("Other.NoPermission.Title")), 20, 40, 20);
                        plugin.getNMS().sendTitle(player, Utility.color(config.getString("Other.NoPermission.SubTitle")), 20, 40, 20);
                    }
                    return true;
                }
            }
            if (args[0].equalsIgnoreCase("setTime")) {
                if (sender.hasPermission("timedfly.admin")) {
                    if (args.length == 1 || args.length == 2) {
                        utility.message(sender, config.getString("Other.SetTime.Usage"));
                        return true;
                    }
                    int gettime = Integer.parseInt(args[1]);
                    int time = Integer.parseInt(args[2]);
                    if (itemscf.contains("Items." + gettime)) {
                        itemscf.set("Items." + gettime + ".Time", time);
                        items.saveItems();
                        utility.message(sender, config.getString("Other.SetTime.Found")
                                .replace("%time%", Integer.toString(time)).replace("%itemid%", Integer.toString(gettime)));
                    } else {
                        utility.message(sender, config.getString("Other.SetTime.NotFound")
                                .replace("%time%", Integer.toString(time)).replace("%itemid%", Integer.toString(gettime)));
                    }
                } else {
                    utility.message(sender, config.getString("Other.NoPermission.Message"));
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        plugin.getNMS().sendTitle(player, Utility.color(config.getString("Other.NoPermission.Title")), 20, 40, 20);
                        plugin.getNMS().sendTitle(player, Utility.color(config.getString("Other.NoPermission.SubTitle")), 20, 40, 20);
                    }
                    return true;
                }
            }
            if (args[0].equalsIgnoreCase("setPrice")) {
                if (sender.hasPermission("timedfly.admin")) {
                    if (args.length == 1 || args.length == 2) {
                        utility.message(sender, config.getString("Other.SetPrice.Usage"));
                        return true;
                    }
                    int getprice = Integer.parseInt(args[1]);
                    int price = Integer.parseInt(args[2]);
                    if (itemscf.contains("Items." + getprice)) {
                        itemscf.set("Items." + getprice + ".Price", price);
                        items.saveItems();
                        utility.message(sender, config.getString("Other.SetPrice.Found")
                                .replace("%price%", Integer.toString(price)).replace("%itemid%", Integer.toString(getprice)));
                    } else {
                        utility.message(sender, config.getString("Other.SetPrice.NotFound")
                                .replace("%price%", Integer.toString(price)).replace("%itemid%", Integer.toString(getprice)));
                    }
                } else {
                    utility.message(sender, config.getString("Other.NoPermission.Message"));
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        plugin.getNMS().sendTitle(player, Utility.color(config.getString("Other.NoPermission.Title")), 20, 40, 20);
                        plugin.getNMS().sendTitle(player, Utility.color(config.getString("Other.NoPermission.SubTitle")), 20, 40, 20);
                    }
                    return true;
                }
            }
            if (args[0].equalsIgnoreCase("permissions")) {
                sender.sendMessage(Utility.color("&8&m----------------------------------------"));
                sender.sendMessage(Utility.color("&c&l             TimedFly Permissions"));
                sender.sendMessage(Utility.color(""));
                sender.sendMessage(Utility.color("&6» &atimedfly.admin &7- access to all commands"));
                sender.sendMessage(Utility.color("&6» &atimedfly.fly.set &7-  access to set fly to another player"));
                sender.sendMessage(Utility.color("&6» &atimedfly.fly.add &7-  access to add time to another player"));
                sender.sendMessage(Utility.color("&6» &atimedfly.fly.onoff &7- access to enable or disable fly for your self"));
                sender.sendMessage(Utility.color("&6» &atimedfly.limit.bypass &7- bypass the allowed time limit"));
                sender.sendMessage(Utility.color("&6» &atimedfly.help &7- access to see the help of the plugin"));
                sender.sendMessage(Utility.color("&6» &atimedfly.fly.stopresume &7- access to save, hide and resume the flight time left"));
                sender.sendMessage(Utility.color(""));
                sender.sendMessage(Utility.color("&8&m----------------------------------------"));
                return true;
            }
            if (args[0].equalsIgnoreCase("setItem")) {
                if (!(sender instanceof Player)) {
                    utility.message(sender, "&cOnly players can do this");
                    return true;
                }
                if (sender.hasPermission("timedfly.admin")) {
                    Player player = (Player) sender;
                    if (args.length == 1) {
                        utility.message(sender, config.getString("Other.SetItem.Usage"));
                        return true;
                    }
                    int itemid = Integer.parseInt(args[1]);
                    int data = player.getItemInHand().getDurability();
                    int ammount = player.getItemInHand().getAmount();
                    int id = player.getItemInHand().getTypeId();
                    if (itemscf.contains("Items." + itemid)) {
                        itemscf.set("Items." + itemid + ".Material", id);
                        itemscf.set("Items." + itemid + ".Data", data);
                        itemscf.set("Items." + itemid + ".Ammount", ammount);
                        items.saveItems();
                        utility.message(sender, config.getString("Other.SetItem.Found").replace("%itemid%", Integer.toString(itemid)));
                    } else {
                        utility.message(sender, config.getString("Other.SetItem.NotFound").replace("%itemid%", Integer.toString(itemid)));
                    }
                } else {
                    utility.message(sender, config.getString("Other.NoPermission.Message"));
                    Player player = (Player) sender;
                    plugin.getNMS().sendTitle(player, Utility.color(config.getString("Other.NoPermission.Title")), 20, 40, 20);
                    plugin.getNMS().sendTitle(player, Utility.color(config.getString("Other.NoPermission.SubTitle")), 20, 40, 20);
                    return true;
                }
            }
        }
        return true;
    }

    private void reloadFiles() {
        plugin.reloadConfig();
        lang.reloadLang();
        lang.createFiles(plugin);
        items.createFiles(plugin);
        items.reloadItems();
        updateConfig.updateConfig(plugin);
        configCache.loadConfiguration();
    }
}
