package me.jackscode.timedfly.managers;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

public class PlaceholderManager {

    @Getter
    final Map<String, String> placeholders;

    public PlaceholderManager() {
        this.placeholders = new HashMap<>();
    }

    public String getValue(String string) {
        String placeholder = this.placeholders.get(string);

        if (placeholder == null) {
            return string;
        }

        return placeholder;
    }

    public String replacePlaceholders(String message) {
        String replaced = message;
        for (Map.Entry<String, String> entry : this.placeholders.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            replaced = replaced.replace(key, value);
        }
        return replaced;
    }

    public void add(String placeholder, String value) {
        this.placeholders.put(placeholder, value);
    }

}
