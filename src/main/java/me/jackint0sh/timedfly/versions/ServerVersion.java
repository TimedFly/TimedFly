package me.jackint0sh.timedfly.versions;

import me.jackint0sh.timedfly.utilities.MessageUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.tr7zw.changeme.nbtapi.NBTItem;


public abstract class ServerVersion {

    protected static ServerVersion serverVersion;

    public void sendActionBar(Player player, String text) {
        //player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(MessageUtil.color(text)));
    }

    public void sendTitle(Player player, String title, String subtile) {
        //player.sendTitle(MessageUtil.color(title), MessageUtil.color(subtile), 0, 5 * 20, 0);
    }

    public void sendTitle(Player player, String title, String subtile, int fadeIn, int stay, int fadeOut) {
        //player.sendTitle(MessageUtil.color(title), MessageUtil.color(subtile), fadeIn, stay, fadeOut);
    }

    public ItemStack setNBT(ItemStack itemStack, String key, String value) {
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setString(key, value);
        return nbtItem.getItem();
    }

    public boolean hasTag(ItemStack itemStack, String key) {
        NBTItem nbtItem = new NBTItem(itemStack);
        return nbtItem.hasKey(key);
    }

    public String getTag(ItemStack itemStack, String key) {
        NBTItem nbtItem = new NBTItem(itemStack);
        return nbtItem.getString(key);
    }

    public static ServerVersion getSupportedVersion() {
        return serverVersion;
    }
}
