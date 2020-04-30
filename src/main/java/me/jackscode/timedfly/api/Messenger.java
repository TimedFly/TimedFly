package me.jackscode.timedfly.api;

import lombok.Getter;
import me.jackscode.timedfly.api.entity.TFConsole;
import me.jackscode.timedfly.api.entity.TFPlayer;

public abstract class Messenger {

    public abstract boolean sendMessage(String message);

    public abstract boolean sendMessage(String[] message);

    @Getter public TFConsole consoleSender;

    @Getter public TFPlayer tfPlayer;

    public boolean isConsole() {
        return consoleSender != null;
    }

}
