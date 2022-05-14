package me.jackint0sh.timedfly.flygui;

import me.jackint0sh.timedfly.hooks.currencies.Item;
import me.jackint0sh.timedfly.managers.CurrencyManager;
import me.jackint0sh.timedfly.utilities.Config;
import me.jackint0sh.timedfly.interfaces.Currency;
import me.jackint0sh.timedfly.utilities.MessageUtil;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class FlyItem {
    private String key;
    private String name;
    private String material;
    private String permission;
    private String permissionMessage;
    private String time;
    private String cooldown;
    private int slot;
    private int data;
    private int amount;
    private int price;
    private List<String> lore;
    private List<String> onClick;
    private List<String> onFlyDisable;
    private boolean hideAttributes;
    private boolean hideEnchants;
    private boolean hidePlaceOn;
    private boolean hidePotionEffects;
    private boolean hideUnbreakable;
    private boolean glow;
    private boolean usePerms;
    private boolean enableOnClick;
    private boolean enableOnFlyDisable;
    private Currency currency;
    private Material currency_item;
    private double flySpeedMultiplier;

    private static Map<String, FlyItem> configItemMap = new HashMap<>();

    public FlyItem() {
        this.key = UUID.randomUUID().toString();
        this.name = "";
        this.cooldown = "";
        this.permission = "";
        this.permissionMessage = "";
        this.material = "";
        this.lore = new ArrayList<>();
        this.onClick = new ArrayList<>();
        this.price = 0;
        this.slot = -1;
        this.time = "0";
        this.amount = 1;
        this.currency = CurrencyManager.getDefaultCurrency();
        this.currency_item = Material.DIAMOND;
        this.flySpeedMultiplier = 10.0F;

        FlyItem.configItemMap.put(key, this);
    }

    public FlyItem(String key) {
        try {
            FileConfiguration config = Config.getConfig("items").get();
            String itemKey = "Items." + key;

            this.key = key;
            this.name = config.getString(itemKey + ".Name");
            this.material = config.getString(itemKey + ".Material");
            this.permission = config.getString(itemKey + ".Permission");
            this.permissionMessage = config.getString(itemKey + ".PermissionMesssage");
            this.time = config.getString(itemKey + ".Time");
            this.cooldown = config.getString(itemKey + ".Cooldown");
            this.slot = config.getInt(itemKey + ".Slot");
            this.data = config.getInt(itemKey + ".Data");
            this.amount = config.getInt(itemKey + ".Amount");
            this.price = config.getInt(itemKey + ".Price");
            this.lore = config.getStringList(itemKey + ".Lore");
            this.onClick = config.getStringList(itemKey + ".OnClick.Commands");
            this.enableOnClick = config.getBoolean(itemKey + ".OnClick.Enable");
            this.hideAttributes = config.getBoolean(itemKey + ".Hide_Attributes");
            this.hideEnchants = config.getBoolean(itemKey + ".Hide_Enchants");
            this.hidePlaceOn = config.getBoolean(itemKey + ".Hide_Place_On");
            this.hidePotionEffects = config.getBoolean(itemKey + ".Hide_Potion_Effects");
            this.hideUnbreakable = config.getBoolean(itemKey + ".Hide_Unbreakable");
            this.glow = config.getBoolean(itemKey + ".Glow");
            this.usePerms = config.getBoolean(itemKey + ".UsePermission");
            this.onFlyDisable = config.getStringList(itemKey + ".OnFlyDisable.Commands");
            this.enableOnFlyDisable = config.getBoolean(itemKey + ".OnFlyDisable.Enable");
            this.currency = CurrencyManager.getCurrency(config.getString(itemKey + ".Currency.Type"));
            this.currency_item = Material.matchMaterial(config.getString(itemKey + ".Currency.Item"));
            this.flySpeedMultiplier = config.getDouble(itemKey + ".FlySpeedMultiplier");

            FlyItem.configItemMap.put(key, this);
        } catch (Exception e) {
            MessageUtil.sendConsoleMessage("There was an error while trying to add item with key: " + key);
            e.printStackTrace();
        }
    }

    public static FlyItem setConfigItem(FlyItem flyItem) {
        return configItemMap.put(flyItem.getKey(), flyItem);
    }

    public static FlyItem removeConfigItem(String key) {
        return configItemMap.remove(key);
    }

    public static FlyItem getConfigItem(String key) {
        return configItemMap.get(key);
    }

    public static Map<String, FlyItem> getConfigItems() {
        return configItemMap;
    }

    public FlyItem save() {
        configItemMap.put(key, this);
        return configItemMap.get(key);
    }

    public String getKey() {
        return key;
    }

    public FlyItem setKey(String key) {
        this.key = key;
        return this;
    }

    public String getName() {
        return name;
    }

    public FlyItem setName(String name) {
        this.name = name;
        return this;
    }

    public String getMaterial() {
        return material;
    }

    public FlyItem setMaterial(String material) {
        this.material = material;
        return this;
    }

    public String getPermission() {
        return permission;
    }

    public FlyItem setPermission(String permission) {
        this.permission = permission;
        return this;
    }

    public String getPermissionMessage() {
        return permissionMessage;
    }

    public FlyItem setPermissionMessage(String permissionMessage) {
        this.permissionMessage = permissionMessage;
        return this;
    }

    public String getTime() {
        return time;
    }

    public FlyItem setTime(String time) {
        this.time = time;
        return this;
    }

    public String getCooldown() {
        return cooldown;
    }

    public FlyItem setCooldown(String cooldown) {
        this.cooldown = cooldown;
        return this;
    }

    public int getSlot() {
        return slot;
    }

    public FlyItem setSlot(int slot) {
        this.slot = slot;
        return this;
    }

    public int getData() {
        return data;
    }

    public FlyItem setData(int data) {
        this.data = data;
        return this;
    }

    public int getAmount() {
        return amount;
    }

    public FlyItem setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public int getPrice() {
        return price;
    }

    public FlyItem setPrice(int price) {
        this.price = price;
        return this;
    }

    public List<String> getLore() {
        return lore;
    }

    public FlyItem setLore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public List<String> getOnClick() {
        return onClick;
    }

    public FlyItem setOnClick(List<String> onClick) {
        this.onClick = onClick;
        return this;
    }

    public boolean isHideAttributes() {
        return hideAttributes;
    }

    public FlyItem setHideAttributes(boolean hideAttributes) {
        this.hideAttributes = hideAttributes;
        return this;
    }

    public boolean isHideEnchants() {
        return hideEnchants;
    }

    public FlyItem setHideEnchants(boolean hideEnchants) {
        this.hideEnchants = hideEnchants;
        return this;
    }

    public boolean isHidePlaceOn() {
        return hidePlaceOn;
    }

    public FlyItem setHidePlaceOn(boolean hidePlaceOn) {
        this.hidePlaceOn = hidePlaceOn;
        return this;
    }

    public boolean isHidePotionEffects() {
        return hidePotionEffects;
    }

    public FlyItem setHidePotionEffects(boolean hidePotionEffects) {
        this.hidePotionEffects = hidePotionEffects;
        return this;
    }

    public boolean isHideUnbreakable() {
        return hideUnbreakable;
    }

    public FlyItem setHideUnbreakable(boolean hideUnbreakable) {
        this.hideUnbreakable = hideUnbreakable;
        return this;
    }

    public boolean isGlow() {
        return glow;
    }

    public FlyItem setGlow(boolean glow) {
        this.glow = glow;
        return this;
    }

    public boolean isUsePerms() {
        return usePerms;
    }

    public FlyItem setUsePerms(boolean usePerms) {
        this.usePerms = usePerms;
        return this;
    }

    public Currency getCurrency() {
        if (this.currency instanceof Item) {
            ((Item) this.currency).setMaterial(this.currency_item);
        }
        return this.currency;
    }

    public void setCurrency(String currency) {
        this.currency = CurrencyManager.getCurrency(currency);
    }

    public List<String> getOnFlyDisable() {
        return onFlyDisable;
    }

    public FlyItem setOnFlyDisable(List<String> onFlyDisable) {
        this.onFlyDisable = onFlyDisable;
        return this;
    }

    public boolean isEnableOnClick() {
        return enableOnClick;
    }

    public FlyItem setEnableOnClick(boolean enableOnClick) {
        this.enableOnClick = enableOnClick;
        return this;
    }

    public boolean isEnableOnFlyDisable() {
        return enableOnFlyDisable;
    }

    public FlyItem setEnableOnFlyDisable(boolean enableOnFlyDisable) {
        this.enableOnFlyDisable = enableOnFlyDisable;
        return this;
    }
    
    public double getFlySpeedMultiplier() {
        return flySpeedMultiplier;
    }

    public void setFlySpeedMultiplier(float flySpeedMultiplier) {
        this.flySpeedMultiplier = flySpeedMultiplier;
    }

    @Override
    public String toString() {
        return "FlyItem{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", material='" + material + '\'' +
                ", permission='" + permission + '\'' +
                ", permissionMessage='" + permissionMessage + '\'' +
                ", time='" + time + '\'' +
                ", cooldown='" + cooldown + '\'' +
                ", slot=" + slot +
                ", data=" + data +
                ", amount=" + amount +
                ", price=" + price +
                ", lore=" + lore +
                ", onClick=" + onClick +
                ", hideAttributes=" + hideAttributes +
                ", hideEnchants=" + hideEnchants +
                ", hidePlaceOn=" + hidePlaceOn +
                ", hidePotionEffects=" + hidePotionEffects +
                ", hideUnbreakable=" + hideUnbreakable +
                ", glow=" + glow +
                ", usePerms=" + usePerms +
                ", currency=" + currency +
                ", flySpeedMultiplier=" + flySpeedMultiplier +
                ", onFlyDisable=" + onFlyDisable +
                ", enableOnClick=" + enableOnClick +
                ", enableOnFlyDisable=" + enableOnFlyDisable +
                '}';
    }
}
