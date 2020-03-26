package me.jackint0sh.timedfly.utilities;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.FileReader;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;

public class Languages {

    private static Plugin plugin = Bukkit.getPluginManager().getPlugin("TimedFly");
    private static Map<String, JsonElement> files = new Hashtable<>();
    private static String lang = Config.getConfig("config").get().getString("Language");

    public static void loadLang() {
        try {
            String path = plugin.getDataFolder() + "/languages/";
            files.put(lang, new JsonParser().parse(new FileReader(path + lang + ".json")));
            if (!lang.equals("english")) {
                files.put("english", new JsonParser().parse(new FileReader(path + "english.json")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createFiles() {
        String[] languages = {"english"};
        Arrays.stream(languages).forEach(language -> {
            plugin.saveResource("languages/" + language + ".json", true);
        });
    }

    public static Object get(String path) {
        String not_found = "Path '" + path + "' not found!";
        String[] paths = {path};
        if (path.contains(".")) paths = path.split("\\.");

        JsonElement element = get(paths, files.get(lang), files.get("english"));
        if (element == null || element.isJsonObject()) return not_found;
        else if (element.isJsonNull()) return null;
        else if (!element.isJsonArray()) return element.getAsString();
        else return new Gson().fromJson(element, String[].class);
    }

    public static String getString(String path) {
        String not_found = "Path '" + path + "' not found!";
        String[] paths = {path};
        if (path.contains(".")) paths = path.split("\\.");

        JsonElement element = get(paths, files.get(lang), files.get("english"));
        if (element == null || element.isJsonObject()) return not_found;
        else if (element.isJsonNull()) return null;
        else return element.getAsString();
    }

    public static String[] getStringArray(String path) {
        String not_found = "Path '" + path + "' not found!";
        String[] paths = {path};
        if (path.contains(".")) paths = path.split("\\.");

        JsonElement element = get(paths, files.get(lang), files.get("english"));
        if (element == null || element.isJsonObject()) return new String[]{not_found};
        else if (element.isJsonNull()) return null;
        else return new Gson().fromJson(element, String[].class);
    }

    private static JsonElement get(String[] path, JsonElement element, JsonElement defElement) {
        if (element == null) return null;
        if (!element.isJsonObject()) return element;

        JsonObject object = element.getAsJsonObject();
        JsonObject def = defElement.getAsJsonObject();

        JsonElement el = object.get(path[0]);
        JsonElement df = def.get(path[0]);

        if (!object.has(path[0]) || el == null) {
            if (!files.get(lang).getAsJsonObject().get("language").getAsString().equals("english")) {
                if (def.has(path[0])) el = df;
                else return null;
            } else return null;
        } else if (!el.isJsonObject()) return el;
        return get(Arrays.copyOfRange(path, 1, path.length), el, df);
    }
}
