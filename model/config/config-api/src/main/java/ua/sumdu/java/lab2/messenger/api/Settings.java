package ua.sumdu.java.lab2.messenger.api;

import java.util.Map;

public interface Settings {

    void putSetting(String setting, String value);

    void removeSetting(String setting);

    Map<String, String> getSettingsMap();

}
