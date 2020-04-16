package me.jackscode.timedfly.enums;

public enum CommandType {

    TIMED_FLY("tf"),
    TFLY("tfly");

    String name;

    CommandType(String commandName){
        this.name = commandName;
    }

}
