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
    public boolean sendMessage(String... messages) {
        String[] msgs = Arrays.stream(messages)
                .map(message -> {
                    String colored = ChatColor.translateAlternateColorCodes('&', message);
                    return this.replacePlaceholders(colored);
                })
                .toArray(String[]::new);

        this.console.sendMessage(msgs);
        return true;
    }

    @Override
    public boolean hasPermission(String... permissions) {
        return true;
    }
}
