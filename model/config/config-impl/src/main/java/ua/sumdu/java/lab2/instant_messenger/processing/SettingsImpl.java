package ua.sumdu.java.lab2.instant_messenger.processing;


import ua.sumdu.java.lab2.instant_messenger.api.Settings;

import java.util.HashMap;
import java.util.Map;

public class SettingsImpl implements Settings {

    private Map<String, String> settingsMap = new HashMap<String, String>();

    public void putSetting (String setting, String value) {
        settingsMap.put(setting, value);
    }

    public void removeSetting (String setting) {
        settingsMap.remove(setting);
    }

    public Map<String, String> getSettingsMap() {
        return settingsMap;
    }

    public void setSettingsMap(Map<String, String> settingsMap) {
        this.settingsMap = settingsMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SettingsImpl settings = (SettingsImpl) o;

        return settingsMap != null ? settingsMap.equals(settings.settingsMap) : settings.settingsMap == null;
    }

}
