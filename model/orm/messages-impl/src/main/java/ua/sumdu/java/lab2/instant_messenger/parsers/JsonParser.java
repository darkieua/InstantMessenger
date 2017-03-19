package ua.sumdu.java.lab2.instant_messenger.parsers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.java.lab2.instant_messenger.api.MessageMap;
import ua.sumdu.java.lab2.instant_messenger.api.MessageMapParser;

import java.io.File;

public final class JsonParser implements MessageMapParser{
    private static final Logger LOG = LoggerFactory.getLogger(JsonParser.class);

    private static JsonParser instance;

    private JsonParser() {
    }

    public static JsonParser getInstance() {
        synchronized (JsonParser.class) {
            LOG.debug("Create a new UserCreator");
            if (instance == null) {
                instance = new JsonParser();
            }
            return instance;
        }
    }

    @Override
    public boolean write(MessageMap map, File file) {
        return false;
    }

    @Override
    public MessageMap read(File file) {
        return null;
    }
}
