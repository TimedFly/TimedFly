package me.jackscode.timedfly.api;

import lombok.Getter;

public abstract class Messenger {

    public abstract boolean sendMessage(String message);

    public abstract boolean sendMessage(String[] message);

    @Getter public TFConsole consoleSender;

    @Getter public TFPlayer tfPlayer;

    public boolean isConsole() {
        return consoleSender != null;
    }

}
