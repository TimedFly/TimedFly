package me.jackint0sh.timedfly.versions.v1_11;

import me.jackint0sh.timedfly.versions.ServerVersion;
import net.minecraft.server.v1_11_R1.IChatBaseComponent;
import net.minecraft.server.v1_11_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_11_R1.PacketPlayOutChat;
import net.minecraft.server.v1_11_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_11_R1.PacketPlayOutTitle.EnumTitleAction;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class v1_11_R1 extends ServerVersion {

    public v1_11_R1() {
        serverVersion = this;
    }

    @Override
    public void sendActionBar(Player player, String text) {
        IChatBaseComponent chatBaseComponent = ChatSerializer.a("{\"text\": \"" + text + "\"}");
        PacketPlayOutChat bar = new PacketPlayOutChat(chatBaseComponent, (byte) 2);

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(bar);
    }

    @Override
    public void sendTitle(Player player, String title, String subtitle) {
        this.sendTitle(player, title, subtitle, 0, 5 * 20, 0);
    }

    @Override
    public void sendTitle(Player player, String title, String subtile, int fadeIn, int stay, int fadeOut) {
        IChatBaseComponent chatTitle = ChatSerializer.a("{\"text\": \"" + title + "\",\"color\":\"white\"}");
        IChatBaseComponent chatSubTitle = ChatSerializer.a("{\"text\": \"" + title + "\",\"color\":\"white\"}");

        PacketPlayOutTitle packetTitle = new PacketPlayOutTitle(EnumTitleAction.TITLE, chatTitle);
        PacketPlayOutTitle packetSubtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, chatSubTitle);
        PacketPlayOutTitle length = new PacketPlayOutTitle(fadeIn, stay, fadeOut);

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetTitle);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetSubtitle);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(length);
    }
}
