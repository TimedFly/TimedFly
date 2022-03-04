package me.jackscode.timedfly.api;

import javax.annotation.Nullable;

import lombok.Getter;

public class Argument {
    @Getter
    private final ArgumentType type;
    @Getter
    private final String name;
    @Getter
    private final String desctiption;
    @Getter
    private final String example;

    public Argument(String name, ArgumentType type, String description, @Nullable String example) {
        this.type = type;
        this.name = name;
        this.desctiption = description;
        this.example = example;
    }

    public Argument(String name, ArgumentType type, String description) {
        this.type = type;
        this.name = name;
        this.desctiption = description;
        this.example = null;
    }

    @Override
    public String toString() {
        if (this.type == ArgumentType.OPTIONAL) {
            return "[%s]".formatted(this.name);
        } else if (this.type == ArgumentType.REQUIRED) {
            return "<%s>".formatted(this.name);
        }
        return "";
    }
}
