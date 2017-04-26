package ua.sumdu.java.lab2.messenger.config.tests;

import static org.junit.Assert.assertTrue;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import ua.sumdu.java.lab2.messenger.api.Settings;
import ua.sumdu.java.lab2.messenger.api.SettingsParser;
import ua.sumdu.java.lab2.messenger.processing.SettingsImpl;
import ua.sumdu.java.lab2.messenger.processing.SettingsParserImpl;

@RunWith(DataProviderRunner.class)

public class SettingsTest {

    /**
     * Data for tests.
     */
    @DataProvider
    public static Object[][] data() {
        Settings settings = new SettingsImpl();
        settings.putSetting("setting1", "value1");
        settings.putSetting("setting2", "value2");
        return new Object[][]{{settings}};
    }

    @Test
    @UseDataProvider("data")
    public void writeAndReadJsonString(Settings settings) {
        SettingsParser parser = new SettingsParserImpl();
        String json = parser.settingsToJson(settings);
        Settings parsedSettings = parser.jsonToSettings(json);
        assertTrue(parsedSettings.equals(settings));
    }
}
