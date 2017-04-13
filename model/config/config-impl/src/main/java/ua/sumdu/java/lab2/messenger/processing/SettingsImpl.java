package ua.sumdu.java.lab2.messenger.processing;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ua.sumdu.java.lab2.messenger.api.Settings;

public class SettingsImpl implements Settings {

  private Map<String, String> settingsMap = new HashMap<String, String>();

  public void putSetting(String setting, String value) {
    settingsMap.put(setting, value);
  }

  public void removeSetting(String setting) {
    settingsMap.remove(setting);
  }

  public Map<String, String> getSettingsMap() {
    return settingsMap;
  }

  public void setSettingsMap(Map<String, String> settingsMap) {
    this.settingsMap = settingsMap;
  }

  @Override
  public boolean equals(Object obj) {
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
  public int hashCode() {
    return Objects.hash(settingsMap);
  }
}
