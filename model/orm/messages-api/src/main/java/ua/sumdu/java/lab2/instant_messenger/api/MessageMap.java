package ua.sumdu.java.lab2.instant_messenger.api;

import ua.sumdu.java.lab2.instant_messenger.entities.Message;

public interface MessageMap {

    void addMessage(Message message);

    void deleteMessage(Message message);
}
