package ua.sumdu.java.lab2.messenger.handler.test;

import static ua.sumdu.java.lab2.messenger.handler.entities.ResponseType.*;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import ua.sumdu.java.lab2.messenger.entities.*;
import ua.sumdu.java.lab2.messenger.handler.processing.ResponseParsingImpl;
import ua.sumdu.java.lab2.messenger.parsers.XmlParser;
import ua.sumdu.java.lab2.messenger.processing.GroupMapParserImpl;
import ua.sumdu.java.lab2.messenger.processing.UserCreatorImpl;
import ua.sumdu.java.lab2.messenger.processing.UserMapParserImpl;

@RunWith(DataProviderRunner.class)
public class ResponseParsingImplTest {

  ResponseParsingImpl responseParsing;

  @Before
  public void init() {
    responseParsing = new ResponseParsingImpl();
  }

  @DataProvider
  public static Object[][] groupForTest() throws UnknownHostException {
    return RequestParsingImplTest.groupForTest();
  }

  @DataProvider
  public static Object[][] messages() throws UnknownHostException {
    return ResponseGeneratingImplTest.messages();
  }

  @Test
  public void userIsOffline() {
    String response = USER_IS_OFFLINE.getResponseNumber() + "=test";
    responseParsing.responseParsing(response);
    MessageMapImpl messageMap = (MessageMapImpl) XmlParser.INSTANCE.
        read(User.getSystemMessageFile());
    boolean isFind = false;
    for (Message message : messageMap.getMapForMails().values()) {
      if ("User(test) is offline".equals(message.getText())
        && LocalDateTime.now().minusSeconds(1).isBefore(message.getTimeSending())) {
        isFind = true;
        messageMap.deleteMessage(message);
      }
    }
    Assert.assertTrue(isFind);
    XmlParser.INSTANCE.write(messageMap, User.getSystemMessageFile());
  }

  @Test
  public void declinedRequest() {
    String response = REQUEST_HAS_BEEN_DECLINED.getResponseNumber() + "=testUser(192.196.1.2)";
    responseParsing.responseParsing(response);
    MessageMapImpl messageMap = (MessageMapImpl) XmlParser.INSTANCE.read(User.getSystemMessageFile());
    boolean isFind = false;
    for (Message message : messageMap.getMapForMails().values()) {
      if ("User testUser(192.196.1.2)  declined your request".equals(message.getText())
          && LocalDateTime.now().minusSeconds(1).isBefore(message.getTimeSending())) {
        isFind = true;
        messageMap.deleteMessage(message);
      }
    }
    Assert.assertTrue(isFind);
    XmlParser.INSTANCE.write(messageMap, User.getSystemMessageFile());
  }

  @Test
  @UseDataProvider("groupForTest")
  public void updatedGroupList(String chatName, UserMapImpl userMap) {
    GroupMapParserImpl groupMapParser = GroupMapParserImpl.getInstance();
    GroupMapImpl groupMap = (GroupMapImpl) groupMapParser.getGroupMap();
    GroupMapImpl groupForRequest = new GroupMapImpl();
    groupForRequest.getMap().put(chatName, userMap);
    String request = UPDATED_GROUP_LIST.getResponseNumber() + "="
      + groupMapParser.groupMapToJSonString(groupForRequest);
    responseParsing.responseParsing(request);
    GroupMapImpl newGroups = (GroupMapImpl) groupMapParser.getGroupMap();
    groupMap.getMap().put(chatName, userMap);
    Assert.assertEquals(RequestParsingImplTest.getMessage(newGroups.toString(),
      groupMap.toString()), newGroups, groupMap);
    groupMap.getMap().remove(chatName);
    groupMapParser.writeGroupMapToFile(groupMapParser.groupMapToJSonString(groupMap));
  }

  @Test
  @UseDataProvider("messages")
  public void requestedMessages(Message[] messages) throws IOException {
    String sender = messages[0].getSender();
    File senderMessages = new File(User.getUrlMessageDirectory() + "/" + sender + ".xml");
    MessageMapImpl messageMap = (MessageMapImpl) XmlParser.INSTANCE.read(senderMessages);
    MessageMapImpl messagesForResponse = new MessageMapImpl();
    for (Message message : messages) {
      messageMap.addMessage(message);
      messagesForResponse.addMessage(message);
    }
    File temp = File.createTempFile("test", "messagesForResponse",
      new File(User.getUrlMessageDirectory()));
    XmlParser.INSTANCE.write(messageMap, temp);
    String response = REQUESTED_MESSAGES.getResponseNumber() + "="
      + XmlParser.INSTANCE.toXml(XmlParser.INSTANCE.getDocument(temp));
    temp.delete();
    responseParsing.responseParsing(response);
    MessageMapImpl newMessageMap = (MessageMapImpl) XmlParser.INSTANCE.read(senderMessages);
    Assert.assertEquals(RequestParsingImplTest.getMessage(newMessageMap.toString(),
      messageMap.toString()), newMessageMap, messageMap);
    senderMessages.delete();
  }

  @Test
  public void addedToFriends() throws UnknownHostException {
    UserMapImpl userMap = (UserMapImpl) UserMapParserImpl.getInstance().getFriends();
    User newUser = UserCreatorImpl.INSTANCE.createUser(CategoryUsers.FRIEND, "test_user", "test_user@go.com",
      InetAddress.getLocalHost(), 8040);
    String response = ADDED_TO_FRIENDS.getResponseNumber() + "=" + newUser.toJSonString();
    responseParsing.responseParsing(response);
    userMap.addUser(newUser);
    UserMapImpl newUserMap = (UserMapImpl) UserMapParserImpl.getInstance().getFriends();
    Assert.assertEquals(RequestParsingImplTest.getMessage(newUserMap.toString(),  userMap.toString()), newUserMap, userMap);
    userMap.removeUser(newUser);
    UserMapParserImpl.getInstance().writeUserMapToFile(UserMapParserImpl.getInstance().userMapToJSonString(userMap));
    MessageMapImpl messageMap = (MessageMapImpl) XmlParser.INSTANCE.read(User.getSystemMessageFile());
    boolean isFind = false;
    for (Message message : messageMap.getMapForMails().values()) {
      if ("User test_user confirmed your request to friends".equals(message.getText())
          && LocalDateTime.now().minusSeconds(1).isBefore(message.getTimeSending())) {
        isFind = true;
        messageMap.deleteMessage(message);
      }
    }
    Assert.assertTrue(isFind);
    XmlParser.INSTANCE.write(messageMap, User.getSystemMessageFile());
  }

  @Test
  public void addedToGroup() {
    User testUser = User.getCurrentUser();
    GroupMapImpl currentGroups = (GroupMapImpl) GroupMapParserImpl.getInstance().getGroupMap();
    GroupMapImpl newGroup = new GroupMapImpl();
    String chatName = "testGroup";
    currentGroups.addUser(chatName, testUser);
    newGroup.addUser(chatName, testUser);
    String response = ADDED_TO_GROUP.getResponseNumber() + "=" + GroupMapParserImpl.getInstance()
      .groupMapToJSonString(newGroup);
    responseParsing.responseParsing(response);
    GroupMapImpl updatedGroups = (GroupMapImpl) GroupMapParserImpl.getInstance().getGroupMap();
    Assert.assertEquals(RequestParsingImplTest.getMessage(updatedGroups.toString(),  currentGroups.toString()),
      updatedGroups, currentGroups);
    currentGroups.getMap().remove(chatName);
    GroupMapParserImpl.getInstance().writeGroupMapToFile(GroupMapParserImpl.getInstance()
      .groupMapToJSonString(currentGroups));
  }
}