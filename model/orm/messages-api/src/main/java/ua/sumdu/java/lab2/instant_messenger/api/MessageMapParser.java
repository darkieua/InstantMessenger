package ua.sumdu.java.lab2.instant_messenger.api;

import java.io.File;

public interface MessageMapParser {

    boolean messageMapToXML(MessageMap map);

    MessageMap xmlToMessageMap(File file);
}
