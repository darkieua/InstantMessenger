package ua.sumdu.java.lab2.instant_messenger.processing;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.java.lab2.instant_messenger.api.Settings;
import ua.sumdu.java.lab2.instant_messenger.api.SettingsParser;

import java.util.Map;

public class SettingsParserImpl implements SettingsParser {
    public static final Logger LOG = LoggerFactory.getLogger(SettingsParserImpl.class);

    public String settingsToJson(Settings settings) {
        Map<String, String> settingsMap = settings.getSettingsMap();

        JSONObject jsonObject = new JSONObject();
        for (String setting : settingsMap.keySet()) {
            String value = settingsMap.get(setting);
            jsonObject.put(setting, settingsMap.get(setting));
        }
        return jsonObject.toString();
    }

    public Settings jsonToSettings(String json) {
        Settings settings = new SettingsImpl();
        JSONObject jsonObject = new JSONObject(json);

        for (String setting : jsonObject.keySet()) {
            settings.putSetting(setting, (String)jsonObject.get(setting));
        }
        return settings;
    }
}
