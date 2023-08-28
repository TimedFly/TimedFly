package me.jackscode.timedfly.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import me.jackscode.timedfly.api.entity.FlyConsole;
import me.jackscode.timedfly.api.entity.FlyPlayer;
import me.jackscode.timedfly.managers.PlaceholderManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Text;

public abstract class Messenger extends PlaceholderManager {

    public abstract boolean sendMessage(String... messages);

    public abstract boolean hasPermission(String... permissions);

    @Getter
    public FlyConsole consoleSender;

    @Getter
    public FlyPlayer flyPlayer;

    public boolean isConsole() {
        return consoleSender != null;
    }

    public void sendHoverableMessage(String msg, OnClick onclick, String... desc) {
        if (isConsole()) {
            this.sendMessage(msg);
            return;
        }
        TextComponent message = new TextComponent(
                ChatColor.translateAlternateColorCodes('&', "&7[&cTimedFly&7] &f" + msg));
        List<Content> contents = new ArrayList<>();

        Arrays.asList(desc).stream()
                .map(line -> line + "\n")
                .forEach(line -> contents.add(new Text(ChatColor.translateAlternateColorCodes('&', line))));

        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, contents);
        ClickEvent clickEvent = new ClickEvent(onclick.getAction(), onclick.getString());

        message.setHoverEvent(hoverEvent);
        message.setClickEvent(clickEvent);

        flyPlayer.getPlayer().spigot().sendMessage(message);
    }

    public static class OnClick {
        @Getter
        private final ClickEvent.Action action;
        @Getter
        private final String string;

        public OnClick(ClickEvent.Action action, String string) {
            this.action = action;
            this.string = string;
        }
    }

}
