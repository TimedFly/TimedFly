package me.jackint0sh.timedfly.flygui;

import me.jackint0sh.timedfly.utilities.MessageUtil;
import me.jackint0sh.timedfly.versions.ServerVersion;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Item extends ItemStack {

    private Set<ItemFlag> itemFlags;
    private ItemMeta itemMeta;
    private String name;
    private List<String> lore;
    private boolean toggle;
    private Consumer<InventoryClickEvent> clickConsumer;
    private Consumer<InventoryClickEvent> rightClickConsumer;
    private Consumer<InventoryClickEvent> leftClickConsumer;
    private Consumer<InventoryClickEvent> shiftClickConsumer;
    private Consumer<InventoryClickEvent> shiftRightClickConsumer;
    private Consumer<InventoryClickEvent> shiftLeftClickConsumer;

    public Item(FlyItem flyItem) {
        this(Material.valueOf(flyItem.getMaterial().toUpperCase()));
        this.setLore(flyItem.getLore());
        this.setName(flyItem.getName());
        this.setDurability((short) flyItem.getData());
        this.setAmount(flyItem.getAmount());
        this.setKey(flyItem.getKey());
    }

    public Item setKey(String key) {
        ServerVersion.getSupportedVersion().setNBT(this, "key", key);
        return this;
    }

    public Item(String material) {
        this(Material.valueOf(material.toUpperCase()));
    }

    public Item(Material material) {
        super(ServerVersion.getSupportedVersion().setNBT(new ItemStack(material), "customitem", "true"));
        loadItem();
    }

    public Item(ItemStack itemStack) {
        super(itemStack);
        loadItem();
    }

    public boolean toggled() {
        return toggle;
    }

    public Item toggle(boolean b) {
        toggle = b;
        return this;
    }

    private void loadItem() {
        this.itemMeta = this.getItemMeta();
        this.itemFlags = itemMeta.getItemFlags();
        this.name = itemMeta.getDisplayName();
        this.lore = itemMeta.getLore();
    }

    public Item addItemFlag(ItemFlag itemFlag) {
        this.itemFlags.add(itemFlag);
        this.setItemMeta(this.itemMeta);
        return this;
    }

    public Item addItemFlags(ItemFlag[] itemFlags) {
        Arrays.stream(itemFlags).forEach(this::addItemFlag);
        return this;
    }

    public String getName() {
        return name != null ? name : "";
    }

    public Item setName(String name) {
        this.name = MessageUtil.color(name);
        this.itemMeta.setDisplayName(this.name);
        this.setItemMeta(this.itemMeta);
        return this;
    }

    public List<String> getLore() {
        return lore != null ? lore : new ArrayList<>();
    }

    public Item setLore(List<String> lore) {
        this.lore = lore.stream().map(MessageUtil::color).collect(Collectors.toList());
        this.itemMeta.setLore(this.lore);
        this.setItemMeta(this.itemMeta);
        return this;
    }

    public Item setLore(String... lore) {
        setLore(Arrays.asList(lore));
        return this;
    }

    public Item removeLore() {
        this.lore = new ArrayList<>();
        this.setLore(lore);
        return this;
    }

    public Item setMaterial(Material material) {
        this.setType(material);
        return this;
    }

    public Item glow(boolean b) {
        if (b) {
            this.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
            this.addItemFlag(ItemFlag.HIDE_ENCHANTS);
        } else this.removeEnchantment(Enchantment.DURABILITY);
        return this;
    }

    public void give(Player player) {
        player.getInventory().addItem(this);
    }

    public void give(Player player, int slot) {
        player.getInventory().setItem(slot, this);
    }

    public Item addLoreLine(String line) {
        List<String> lore = getLore();
        lore.add(line);
        setLore(lore);
        return this;
    }

    public Item removeLoreLine(int loc) {
        loc--;
        List<String> lore = getLore();
        if (lore != null && lore.size() > 0 && loc < lore.size()) {
            lore.remove(loc);
            setLore(lore);
        }
        return this;
    }

    public Item removeLastLoreLine() {
        List<String> lore = getLore();
        if (lore != null && lore.size() > 0) {
            lore.remove(lore.size() - 1);
            setLore(lore);
        }
        return this;
    }

    public Item insertLoreLine(int loc, String line) {
        loc++;
        List<String> lore = getLore();
        lore.add(loc, line);
        setLore(lore);
        return this;
    }

    public Item onClick(Consumer<InventoryClickEvent> eventConsumer) {
        this.clickConsumer = eventConsumer;
        return this;
    }

    public Item onRightClick(Consumer<InventoryClickEvent> eventConsumer) {
        this.rightClickConsumer = eventConsumer;
        return this;
    }

    public Item onLeftClick(Consumer<InventoryClickEvent> eventConsumer) {
        this.leftClickConsumer = eventConsumer;
        return this;
    }

    public Item onShiftClick(Consumer<InventoryClickEvent> eventConsumer) {
        this.shiftClickConsumer = eventConsumer;
        return this;
    }

    public Item onShiftRightClick(Consumer<InventoryClickEvent> eventConsumer) {
        this.shiftRightClickConsumer = eventConsumer;
        return this;
    }


    public Item onShiftLeftClick(Consumer<InventoryClickEvent> eventConsumer) {
        this.shiftLeftClickConsumer = eventConsumer;
        return this;
    }

    public void callEvent(Event e) {
        if (e instanceof InventoryClickEvent) {
            InventoryClickEvent event = (InventoryClickEvent) e;
            if (this.clickConsumer != null) this.clickConsumer.accept(event);
            if (this.rightClickConsumer != null && event.getClick().isRightClick() && !event.getClick().isShiftClick())
                this.rightClickConsumer.accept(event);
            if (this.leftClickConsumer != null && event.getClick().isLeftClick() && !event.getClick().isShiftClick())
                this.leftClickConsumer.accept(event);

            if (this.shiftClickConsumer != null && event.getClick().isShiftClick())
                this.shiftClickConsumer.accept(event);
            if (this.shiftRightClickConsumer != null && event.getClick().isShiftClick() && event.getClick().isRightClick())
                this.shiftRightClickConsumer.accept(event);
            if (this.shiftLeftClickConsumer != null && event.getClick().isShiftClick() && event.getClick().isLeftClick())
                this.shiftLeftClickConsumer.accept(event);
        }
    }

}
