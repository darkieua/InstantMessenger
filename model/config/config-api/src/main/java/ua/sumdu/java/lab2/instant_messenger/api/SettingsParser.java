package ua.sumdu.java.lab2.instant_messenger.api;

import java.io.File;

public interface SettingsParser {

    public String SettingsToJson (Settings settings);

    public Settings JsonToSettings (String json);
}
