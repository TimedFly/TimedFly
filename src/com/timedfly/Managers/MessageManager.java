package com.timedfly.Managers;

import com.timedfly.ConfigurationFiles.LangFiles;
import org.bukkit.ChatColor;

public enum MessageManager {
    NOMONEY(ChatColor.translateAlternateColorCodes('&', LangFiles.getInstance().getLang().getString("Fly.Message.NoMoney"))),
    NOPERM(ChatColor.translateAlternateColorCodes('&', LangFiles.getInstance().getLang().getString("Other.NoPermission.Message"))),
    DISABLEDWORLD(ChatColor.translateAlternateColorCodes('&', LangFiles.getInstance().getLang().getString("Other.DisabledWorld"))),
    NotEnoughLevels(ChatColor.translateAlternateColorCodes('&', LangFiles.getInstance().getLang().getString("Fly.Message.NoLevels"))),
    NotExpLevels(ChatColor.translateAlternateColorCodes('&', LangFiles.getInstance().getLang().getString("Fly.Message.NoExp")));

    private final String text;

    MessageManager(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
