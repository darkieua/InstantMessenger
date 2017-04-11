package ua.sumdu.java.lab2.instant_messenger.api;


import java.util.Map;

public interface Settings {

    public void putSetting (String setting, String value);

    public void removeSetting (String setting);

    public Map<String, String> getSettingsMap ();


}
