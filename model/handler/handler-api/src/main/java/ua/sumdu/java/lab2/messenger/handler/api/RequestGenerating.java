package ua.sumdu.java.lab2.messenger.handler.api;

import ua.sumdu.java.lab2.messenger.entities.Message;
import ua.sumdu.java.lab2.messenger.entities.SentFiles;

public interface RequestGenerating {

    String creatingFriendsRequest();

    String createJoinRequestToGroup(String groupName);

    String createRequestForNewMessage(Message message);

    String createRequestForNewGroupMessage(Message message);

    String updateGroupList(String groupName);

    String createRequestForUpdateGroupList(String groupName);

    String createRequestForMessagesFromSpecificDate(long date);

    String createRequestForGroupMessagesFromSpecificDate(long date, String groupName);

    String createDataRequest(SentFiles files);

    String creatingDeleteRequestFromFriends();

    String creatingDeleteRequestFromGroup(String groupName);
}
