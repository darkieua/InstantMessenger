package ua.sumdu.java.lab2.instant_messenger.config_impl.tests;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import ua.sumdu.java.lab2.instant_messenger.api.Settings;
import ua.sumdu.java.lab2.instant_messenger.api.SettingsParser;
import ua.sumdu.java.lab2.instant_messenger.processing.SettingsImpl;
import ua.sumdu.java.lab2.instant_messenger.processing.SettingsParserImpl;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

@RunWith(DataProviderRunner.class)

public class SettingsTest {

    @DataProvider
    public static Object[][] data() throws IOException {
        Settings settings = new SettingsImpl();
        settings.putSetting("setting1", "value1");
        settings.putSetting("setting2", "value2");
        return new Object[][]{{settings}};
    }

    @Test
    @UseDataProvider("data")
    public void writeAndReadJsonString(Settings settings) throws IOException {
        SettingsParser parser = new SettingsParserImpl();
        String json = parser.settingsToJson(settings);
        Settings parsed_settings = parser.jsonToSettings(json);
        assertTrue(parsed_settings.equals(settings));
    }
}
