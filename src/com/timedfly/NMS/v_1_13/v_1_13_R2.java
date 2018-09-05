package com.timedfly.NMS.v_1_13;

import com.timedfly.NMS.NMS;
import net.minecraft.server.v1_13_R2.ChatMessageType;
import net.minecraft.server.v1_13_R2.IChatBaseComponent;
import net.minecraft.server.v1_13_R2.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class v_1_13_R2 implements NMS {

    @Override
    public void sendActionbar(Player p, String message) {

        IChatBaseComponent chatBaseComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");

        PacketPlayOutChat bar = new PacketPlayOutChat(chatBaseComponent, ChatMessageType.GAME_INFO);

        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(bar);
    }

    @Override
    public void sendTitle(Player player, String text, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(null, text, fadeIn, stay, fadeOut);
    }

    @Override
    public void sendSubtitle(Player player, String text, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(null, text, fadeIn, stay, fadeOut);
    }

}
