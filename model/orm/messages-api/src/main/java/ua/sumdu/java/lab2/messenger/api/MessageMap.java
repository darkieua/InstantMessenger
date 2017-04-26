package ua.sumdu.java.lab2.messenger.api;

import java.time.LocalDateTime;
import java.util.Map;

import ua.sumdu.java.lab2.messenger.entities.Message;

public interface MessageMap {

    Map<LocalDateTime, Message> getMapForMails();

    void addMessage(Message message);

    void deleteMessage(Message message);
}
