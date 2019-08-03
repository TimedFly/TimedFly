package me.jackint0sh.timedfly.versions.v1_8;

import me.jackint0sh.timedfly.utilities.MessageUtil;
import me.jackint0sh.timedfly.versions.ServerVersion;
import net.minecraft.server.v1_8_R3.*;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;

public class v1_8_R3 extends ServerVersion {

    public v1_8_R3() {
        serverVersion = this;
    }

    @Override
    public void sendActionBar(Player player, String text) {
        IChatBaseComponent chatBaseComponent = ChatSerializer.a("{\"text\": \"" + MessageUtil.color(text) + "\"}");
        PacketPlayOutChat bar = new PacketPlayOutChat(chatBaseComponent, (byte) 2);

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(bar);
    }

    @Override
    public void sendTitle(Player player, String title, String subtitle) {
        this.sendTitle(player, title, subtitle, 0, 5 * 20, 0);
    }

    @Override
    public void sendTitle(Player player, String title, String subtile, int fadeIn, int stay, int fadeOut) {
        IChatBaseComponent chatTitle = ChatSerializer.a("{\"text\": \"" + MessageUtil.color(title) + "\",\"color\":\"white\"}");
        IChatBaseComponent chatSubTitle = ChatSerializer.a("{\"text\": \"" + MessageUtil.color(subtile) + "\",\"color\":\"white\"}");

        PacketPlayOutTitle packetTitle = new PacketPlayOutTitle(EnumTitleAction.TITLE, chatTitle);
        PacketPlayOutTitle packetSubtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, chatSubTitle);
        PacketPlayOutTitle length = new PacketPlayOutTitle(fadeIn, stay, fadeOut);

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetTitle);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetSubtitle);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(length);
    }

    @Override
    public org.bukkit.inventory.ItemStack setNBT(org.bukkit.inventory.ItemStack itemStack, String key, String value) {
        ItemStack stack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = stack.getTag() != null ? stack.getTag() : new NBTTagCompound();

        tag.setString(key, value);
        stack.setTag(tag);

        return CraftItemStack.asCraftMirror(stack);
    }

    @Override
    public boolean hasTag(org.bukkit.inventory.ItemStack itemStack, String key) {
        ItemStack stack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = stack.getTag();

        if (tag == null) return false;
        return !tag.getString(key).isEmpty();
    }
}
