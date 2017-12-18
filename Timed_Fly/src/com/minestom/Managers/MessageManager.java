package com.minestom.Managers;

import com.minestom.ConfigurationFiles.LangFiles;
import com.minestom.TimedFly;
import org.bukkit.ChatColor;

public enum  MessageManager {
    NOMONEY(ChatColor.translateAlternateColorCodes('&', LangFiles.getInstance().getLang().getString("Fly.Message.NoMoney"))),
    NOPERM(ChatColor.translateAlternateColorCodes('&', LangFiles.getInstance().getLang().getString("Other.NoPermission.Message"))),
    DISABLEDWORLD(ChatColor.translateAlternateColorCodes('&',LangFiles.getInstance().getLang().getString("Other.DisabledWorld"))),
    NotEnoughLevels(ChatColor.translateAlternateColorCodes('&',LangFiles.getInstance().getLang().getString("Fly.Message.NoLevels")));

    private final String text;

    MessageManager(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
