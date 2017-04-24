package ua.sumdu.java.lab2.messenger.processing;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ua.sumdu.java.lab2.messenger.api.Settings;

public class SettingsImpl implements Settings {

    private Map<String, String> settingsMap = new HashMap<String, String>();

    public final void putSetting(final String setting, final String value) {
        settingsMap.put(setting, value);
    }

    public final void removeSetting(final String setting) {
        settingsMap.remove(setting);
    }

    public final Map<String, String> getSettingsMap() {
        return settingsMap;
    }

    public final void setSettingsMap(final Map<String, String> settingMap) {
        this.settingsMap = settingMap;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SettingsImpl settings = (SettingsImpl) obj;
        return Objects.equals(settingsMap, settings.settingsMap);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(settingsMap);
    }
}
