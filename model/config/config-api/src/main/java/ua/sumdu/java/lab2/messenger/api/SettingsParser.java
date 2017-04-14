package ua.sumdu.java.lab2.messenger.api;

public interface SettingsParser {

  public String settingsToJson(Settings settings);

  public Settings jsonToSettings(String json);
}
