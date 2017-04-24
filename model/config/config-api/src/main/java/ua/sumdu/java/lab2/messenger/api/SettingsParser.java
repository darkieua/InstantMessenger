package ua.sumdu.java.lab2.messenger.api;

public interface SettingsParser {

    String settingsToJson(Settings settings);

    Settings jsonToSettings(String json);
}
