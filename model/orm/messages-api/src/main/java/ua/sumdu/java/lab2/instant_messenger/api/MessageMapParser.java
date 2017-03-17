package ua.sumdu.java.lab2.instant_messenger.api;

public interface MessageMapParser {

    boolean messageMapToXML(String jsonString);

    boolean XMLToMessageMap(String jsonString);
}
