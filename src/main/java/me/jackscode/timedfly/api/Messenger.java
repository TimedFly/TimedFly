package me.jackscode.timedfly.api;

import lombok.Getter;
import me.jackscode.timedfly.api.entity.TFConsole;
import me.jackscode.timedfly.api.entity.TFPlayer;
import me.jackscode.timedfly.managers.PlaceholderManager;

public abstract class Messenger extends PlaceholderManager {

    public abstract boolean sendMessage(String... messages);

    public abstract boolean hasPermission(String... permissions);

    @Getter public TFConsole consoleSender;

    @Getter public TFPlayer tfPlayer;

    public boolean isConsole() {
        return consoleSender != null;
    }

}
