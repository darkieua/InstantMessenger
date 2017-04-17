package ua.sumdu.java.lab2.messenger.handler.api;

import ua.sumdu.java.lab2.messenger.entities.Message;
import ua.sumdu.java.lab2.messenger.entities.SentFiles;

public interface RequestGenerating {

  String addToFriends();

  String addToGroup(String groupName);

  String newMessage(Message message);

  String newMessageToGroup(Message message);

  String updateGroupList(String groupName);

  String requestForUpdateGroupList(String groupName);

  String messagesFromSpecificDate(long date);

  String groupMessagesFromSpecificDate(long date, String groupName);

  String dataRequest(SentFiles files);
}
