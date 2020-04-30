package me.jackscode.timedfly.api.entity;

import me.jackscode.timedfly.api.Messenger;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

import java.util.Arrays;

public class TFConsole extends Messenger {

    private final ConsoleCommandSender console;

    public TFConsole(ConsoleCommandSender console) {
        this.console = console;
        this.consoleSender = this;
    }

    @Override
    public boolean sendMessage(String message) {
        this.console.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        return true;
    }

    @Override
    public boolean sendMessage(String[] messages) {
        String[] msgs = Arrays.stream(messages)
                .map(message -> ChatColor.translateAlternateColorCodes('&', message))
                .toArray(String[]::new);

        this.console.sendMessage(msgs);
        return true;
    }

}
