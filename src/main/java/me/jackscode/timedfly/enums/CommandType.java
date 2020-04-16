package me.jackscode.timedfly.enums;

import org.jetbrains.annotations.NotNull;

public enum CommandType {

    TIMED_FLY("tf"),
    TFLY("tfly");

    String name;

    CommandType(@NotNull String commandName){
        this.name = commandName;
    }

}
