package me.jackint0sh.timedfly.hooks.currencies;

import me.jackint0sh.timedfly.interfaces.Currency;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class Item implements Currency {

    private Material material;

    @Override
    public String name() {
        return "item";
    }

    @Override
    public boolean withdraw(Player player, int amount) {
        Inventory inventory = player.getInventory();
        int balance = balance(player);
        ItemStack itemStack = new ItemStack(material);
        int newBalance = balance - amount;

        inventory.remove(material);
        if (newBalance > 0) {
            itemStack.setAmount(newBalance);
            inventory.addItem(itemStack);
        }
        return true;
    }

    @Override
    public boolean deposit(Player player, int amount) {
        Inventory inventory = player.getInventory();
        ItemStack itemStack = new ItemStack(material);

        itemStack.setAmount(amount);
        inventory.addItem(itemStack);

        return true;
    }

    @Override
    public boolean has(Player player, int amount) {
        return balance(player) >= amount;
    }

    @Override
    public int balance(Player player) {
        Inventory inventory = player.getInventory();
        Optional<Integer> optionalItemStack = Arrays
                .stream(inventory.getContents())
                .filter(Objects::nonNull)
                .filter(itemStack -> itemStack.getType() == material)
                .map(ItemStack::getAmount)
                .reduce(Integer::sum);

        return optionalItemStack.orElse(0);
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}
