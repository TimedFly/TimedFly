package com.minestom.NMS.v_1_9;

import com.minestom.NMS.NMS;
import net.minecraft.server.v1_9_R2.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class v_1_9_R2 implements NMS {

    @Override
    public void sendActionbar(Player p, String message) {

        IChatBaseComponent chatBaseComponent  = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");

        PacketPlayOutChat bar = new PacketPlayOutChat(chatBaseComponent , (byte) 2);

        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(bar);
    }
    @Override
    public void sendTitle(Player player, String text, int fadeIn, int stay, int fadeOut) {
        IChatBaseComponent chatTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\",color:" + ChatColor.GOLD.name().toLowerCase() + "}");

        PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, chatTitle);
        PacketPlayOutTitle length = new PacketPlayOutTitle(fadeIn, stay, fadeOut);

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(title);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(length);
    }

    @Override
    public void sendSubtitle(Player player, String text, int fadeIn, int stay, int fadeOut) {
        IChatBaseComponent chatTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\",color:" + ChatColor.GOLD.name().toLowerCase() + "}");

        PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, null);
        PacketPlayOutTitle subtitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, chatTitle);
        PacketPlayOutTitle length = new PacketPlayOutTitle(fadeIn, stay, fadeOut);

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(title);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(subtitle);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(length);
    }
    @Override
    public void spawnParticle(Player player, String particle, Location location){
        float x = (float) location.getX();
        float y = (float) location.getY();
        float z = (float) location.getZ();
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.valueOf(particle),true, x,y,z,0,0,0,1,1);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
    }
}