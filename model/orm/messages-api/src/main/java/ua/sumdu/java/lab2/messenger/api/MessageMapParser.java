package ua.sumdu.java.lab2.messenger.api;

import java.io.File;
import java.io.IOException;

public interface MessageMapParser {

    boolean write(MessageMap map, File file) throws IOException;

    MessageMap read(File file) throws IOException;
}
