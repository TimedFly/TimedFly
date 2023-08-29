package me.jackscode.timedfly.api.entity;

import me.jackscode.timedfly.api.Messenger;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

import java.util.Arrays;

public class FlyConsole extends Messenger {

    private final ConsoleCommandSender console;

    public FlyConsole(ConsoleCommandSender console) {
        this.console = console;
    }

    @Override
    public boolean sendMessage(String... messages) {
        String[] msgs = Arrays.stream(messages)
                .map(message -> {
                    message = "&7[&cTimedFly&7] &f" + message;
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
