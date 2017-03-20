package ua.sumdu.java.lab2.instant_messenger.api;

import ua.sumdu.java.lab2.instant_messenger.entities.Message;

import java.io.IOException;

public interface MessageMap {

    void addMessage(Message message) throws IOException;

    void deleteMessage(Message message) throws IOException;
}
