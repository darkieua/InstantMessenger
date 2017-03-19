package ua.sumdu.java.lab2.instant_messenger.api;

import java.io.File;

public interface MessageMapParser {

    boolean write(MessageMap map, File file);

    MessageMap read(File file);
}
