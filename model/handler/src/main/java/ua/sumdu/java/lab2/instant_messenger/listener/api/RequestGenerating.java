package ua.sumdu.java.lab2.instant_messenger.listener.api;

import ua.sumdu.java.lab2.instant_messenger.entities.Message;

public interface RequestGenerating {
    String addToFriends();
    String addToGroup(String groupName);
    String newMessage(Message message);
    String newMessageToGroup(Message message);
    String updateGroupList(String groupName);
    String requestForUpdateGroupList(String groupName);
    String messagesFromSpecificDate(long date);
}
