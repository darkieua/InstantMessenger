package ua.sumdu.java.lab2.messenger.handler.api;

import ua.sumdu.java.lab2.messenger.entities.SentFiles;

public interface RequestGenerating {

    String creatingFriendsRequest();

    String createJoinRequestToGroup(String groupName);

    String updateGroupList(String groupName);

    String createRequestForUpdateGroupList(String groupName);

    String createDataRequest(SentFiles files);

    String creatingDeleteRequestFromFriends();

    String creatingDeleteRequestFromGroup(String groupName);
}
