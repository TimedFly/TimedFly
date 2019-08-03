package me.jackint0sh.timedfly.versions;

import me.jackint0sh.timedfly.utilities.MessageUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public abstract class ServerVersion {

    protected static ServerVersion serverVersion;

    public void sendActionBar(Player player, String text) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(MessageUtil.color(text)));
    }

    public void sendTitle(Player player, String title, String subtile) {
        player.sendTitle(title, subtile, 0, 5 * 20, 0);
    }

    public void sendTitle(Player player, String title, String subtile, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(title, subtile, fadeIn, stay, fadeOut);
    }

    public static ServerVersion getSupportedVersion() {
        return serverVersion;
    }
}
