package ua.sumdu.java.lab2.messenger.handler.processing;

import static ua.sumdu.java.lab2.messenger.handler.entities.RequestType.*;
import static ua.sumdu.java.lab2.messenger.handler.processing.RequestGeneratingImpl.createMessage;

import ua.sumdu.java.lab2.messenger.entities.Message;
import ua.sumdu.java.lab2.messenger.entities.User;
import ua.sumdu.java.lab2.messenger.handler.api.MessageRequestGenerating;

public class MessageRequestGeneratingImpl implements MessageRequestGenerating {

    @Override
    public String createRequestForNewMessage(Message message) {
        StringBuilder str = new StringBuilder();
        str.append(NEW_MESSAGE.getRequestNumber())
                .append('=').append(createMessage(message));
        return str.toString();
    }

    @Override
    public String createRequestForNewGroupMessage(Message message) {
        StringBuilder str = new StringBuilder();
        str.append(NEW_MESSAGE_TO_GROUP.getRequestNumber())
                .append('=').append(createMessage(message));
        return str.toString();
    }

    @Override
    public String createRequestForMessagesFromSpecificDate(long date) {
        StringBuilder str = new StringBuilder();
        str.append(MESSAGES_FROM_A_SPECIFIC_DATE.getRequestNumber())
                .append('=').append(date).append('=').append(User.getCurrentUser().getUsername());
        return str.toString();
    }

    @Override
    public String createRequestForGroupMessagesFromSpecificDate(long date, String groupName) {
        StringBuilder str = new StringBuilder();
        str.append(GROUP_MESSAGES_FROM_A_SPECIFIC_DATE.getRequestNumber())
                .append('=').append(date).append('=').append(groupName);
        return str.toString();
    }
}
