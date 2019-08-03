package me.jackint0sh.timedfly.versions;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Default extends ServerVersion {

    public Default() {
        serverVersion = this;
    }

    @Override
    public void sendActionBar(Player player, String text) {
    }

    @Override
    public void sendTitle(Player player, String title, String subtile) {
    }

    @Override
    public void sendTitle(Player player, String title, String subtile, int fadeIn, int stay, int fadeOut) {
    }

    @Override
    public ItemStack setNBT(ItemStack itemStack, String key, String value) {
        return itemStack;
    }

    @Override
    public boolean hasTag(ItemStack itemStack, String key) {
        return false;
    }
}
