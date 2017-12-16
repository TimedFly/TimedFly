package com.minestom.NMS.v_1_12;

import com.minestom.NMS.NMS;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class v_1_12_R1 implements NMS {

    @Override
    public void sendActionbar(Player p, String message) {

        IChatBaseComponent chatBaseComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");

        PacketPlayOutChat bar = new PacketPlayOutChat(chatBaseComponent, ChatMessageType.GAME_INFO);

        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(bar);
    }

    @Override
    public void sendTitle(Player player, String text, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(text, null, fadeIn, stay, fadeOut);
    }

    @Override
    public void sendSubtitle(Player player, String text, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(null, text, fadeIn, stay, fadeOut);
    }

    @Override
    public void spawnParticle(Player player, String particle, Location location) {
        float x = (float) location.getX();
        float y = (float) location.getY();
        float z = (float) location.getZ();
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.valueOf(particle), true, x, y, z, 0, 0, 0, 1, 1);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}