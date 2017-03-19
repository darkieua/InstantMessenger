package ua.sumdu.java.lab2.instant_messenger.parsers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.java.lab2.instant_messenger.api.MessageMapParser;

import java.io.File;

public class ParserFactory {
    private static final Logger LOG = LoggerFactory.getLogger(ParserFactory.class);

    private static ParserFactory instance;

    private ParserFactory() {
    }

    public static ParserFactory getInstance() {
        synchronized (ParserFactory.class) {
            LOG.debug("Create a new UserCreator");
            if (instance == null) {
                instance = new ParserFactory();
            }
            return instance;
        }
    }

    public MessageMapParser getParser(File file) {
        MessageMapParser parser = null;
        String str = getFileExtension(file);
        if ("json".equals(str)) {
            parser = JsonParser.getInstance();
        } else if ("xml".equals(str)) {
            parser = XMLParser.getInstance();
        }
        return parser;
    }

    private String getFileExtension(File file) {
        return "";
    }
}
