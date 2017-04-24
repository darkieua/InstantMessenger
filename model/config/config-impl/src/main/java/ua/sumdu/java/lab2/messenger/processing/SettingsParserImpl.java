package ua.sumdu.java.lab2.messenger.processing;

import java.util.Map;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.java.lab2.messenger.api.Settings;
import ua.sumdu.java.lab2.messenger.api.SettingsParser;

public class SettingsParserImpl implements SettingsParser {
    public static final Logger LOG = LoggerFactory
            .getLogger(SettingsParserImpl.class);

    /**
     * Converting settings to json string.
     */
    public String settingsToJson(final Settings settings) {
        Map<String, String> settingsMap = settings.getSettingsMap();
        JSONObject jsonObject = new JSONObject();
        for (String setting : settingsMap.keySet()) {
            jsonObject.put(setting, settingsMap.get(setting));
        }
        return jsonObject.toString();
    }

    /**
     * Converting json string to settings.
     */
    public Settings jsonToSettings(final String json) {
        Settings settings = new SettingsImpl();
        JSONObject jsonObject = new JSONObject(json);

        for (String setting : jsonObject.keySet()) {
            settings.putSetting(setting, (String) jsonObject.get(setting));
        }
        return settings;
    }
}
