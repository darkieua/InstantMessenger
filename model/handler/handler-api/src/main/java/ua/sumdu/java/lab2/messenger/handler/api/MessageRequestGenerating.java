package ua.sumdu.java.lab2.messenger.handler.api;

import ua.sumdu.java.lab2.messenger.entities.Message;

public interface MessageRequestGenerating {
    String createRequestForNewMessage(Message message);

    String createRequestForNewGroupMessage(Message message);

    String createRequestForMessagesFromSpecificDate(long date);

    String createRequestForGroupMessagesFromSpecificDate(long date, String groupName);
}
