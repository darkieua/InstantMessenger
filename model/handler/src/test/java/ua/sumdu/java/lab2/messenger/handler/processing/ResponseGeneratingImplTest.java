package ua.sumdu.java.lab2.messenger.handler.processing;

import static ua.sumdu.java.lab2.messenger.entities.CategoryUsers.*;
import static ua.sumdu.java.lab2.messenger.entities.User.CURRENT_USER;
import static ua.sumdu.java.lab2.messenger.handler.entities.ResponseType.*;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import ua.sumdu.java.lab2.messenger.entities.*;
import ua.sumdu.java.lab2.messenger.parsers.XmlParser;
import ua.sumdu.java.lab2.messenger.processing.GroupMapParserImpl;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.Assert.*;

@RunWith(DataProviderRunner.class)
public class ResponseGeneratingImplTest {

  ResponseGeneratingImpl responseGenerating;

  @Before
  public void init() {
    responseGenerating = new ResponseGeneratingImpl();
  }

  @DataProvider
  public static Object[][] shortResponsesWithoutParsing() {
    return new Object[][]{{String.valueOf(SUCCESSFUL.getResponseNumber())}, {String.valueOf(SUCCESSFUL.getResponseNumber())}};
  }

  @DataProvider
  public static Object[][] tempGroup() throws UnknownHostException {
    User[] users = {new User(FRIEND, "user1", "user1@ex.so", 8080, InetAddress.getLocalHost()),
        new User(BLACKLIST, "user2", "user2@ex.so", 8080, InetAddress.getLocalHost()),
        new User(BLACKLIST, "user3", "user3@ex.so", 8080, InetAddress.getLocalHost()),
        new User(BLACKLIST, "user4", "user4@ex.so", 8080, InetAddress.getLocalHost()),
        new User(BLACKLIST, "user5", "user5@ex.so", 8080, InetAddress.getLocalHost())};
    UserMapImpl userMap = new UserMapImpl();
    String nameChat = "testChat";
    for (User user : users) {
      userMap.addUser(user);
    }
    return new Object[][]{{nameChat, userMap}};
  }

  @Test
  @UseDataProvider("shortResponsesWithoutParsing")
  public void nonProcessingShortResponses(String request){
    String result = responseGenerating.responseGenerate(request);
    assertEquals(RequestParsingImplTest.getMessage(result, request), result, request);
  }

  @Test
  public void declinedRequest() {
    String request = String.valueOf(REQUEST_HAS_BEEN_DECLINED.getResponseNumber());
    String result = responseGenerating.responseGenerate(request);
    String correctResult = request + "=" + CURRENT_USER.getUsername() + "(" + CURRENT_USER.getIpAddress() + ")";
    assertEquals(RequestParsingImplTest.getMessage(result, correctResult), result, correctResult);
  }

  @Test
  public void addedToFriend() {
    String request = String.valueOf(ADDED_TO_FRIENDS.getResponseNumber());
    String result = responseGenerating.responseGenerate(request);
    String correctResult = request + "=" + CURRENT_USER.setCategory(CategoryUsers.FRIEND).toJSonString();
    assertEquals(RequestParsingImplTest.getMessage(result, correctResult), result, correctResult);
  }

  @Test
  @UseDataProvider("tempGroup")
  public void updatedGroupList(String chatName, UserMapImpl userMap) {
    GroupMapImpl currentGroups = (GroupMapImpl) GroupMapParserImpl.getInstance().getGroupMap();
    currentGroups.getMap().put(chatName, userMap);
    GroupMapParserImpl.getInstance().writeGroupMapToFile(GroupMapParserImpl.getInstance().groupMapToJSonString(currentGroups));
    String request = UPDATED_GROUP_LIST.getResponseNumber() + "=" + chatName;
    String result = responseGenerating.responseGenerate(request);
    GroupMapImpl groupForResponse = new GroupMapImpl();
    groupForResponse.getMap().put(chatName, userMap);
    String correctResult = UPDATED_GROUP_LIST.getResponseNumber() + "=" + GroupMapParserImpl.getInstance().groupMapToJSonString(groupForResponse);
    assertEquals(RequestParsingImplTest.getMessage(result, correctResult), result, correctResult);
    currentGroups.getMap().remove(chatName);
    GroupMapParserImpl.getInstance().writeGroupMapToFile(GroupMapParserImpl.getInstance().groupMapToJSonString(currentGroups));
  }

  @DataProvider
  public static Object[][] messages() throws UnknownHostException {
    Message[] messages =  {new Message("test_user", CURRENT_USER.getUsername(), "text1", LocalDateTime.now()),
      new Message("test_user", CURRENT_USER.getUsername(), "text2", LocalDateTime.now().minusDays(1)),
      new Message("test_user", CURRENT_USER.getUsername(), "text3", LocalDateTime.now().minusDays(2)),
      new Message("test_user", CURRENT_USER.getUsername(), "text4", LocalDateTime.now().minusDays(3))};
    return new Object[][]{{messages}};
  }

  @Test
  @UseDataProvider("messages")
  public void requestedMessages(Message[] messages) throws IOException {
    MessageMapImpl messageMap = new MessageMapImpl();
    for (Message mess : messages) {
      messageMap.addMessage(mess);
    }
    File testFile = new File(User.getUrlMessageDirectory() + "/" + messages[0].getSender() + ".xml");
    testFile.createNewFile();
    XmlParser.INSTANCE.write(messageMap, testFile);
    long date = messages[1].getTimeSending().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    String request = REQUESTED_MESSAGES.getResponseNumber() + "=" + (date-1) + "=" + messages[0].getSender();
    String result = responseGenerating.responseGenerate(request);
    MessageMapImpl newMap = new MessageMapImpl();
    for (int i = 0; i < 2; i++) {
      newMap.addMessage(messages[i]);
    }
    String correctResult = REQUESTED_MESSAGES.getResponseNumber() + "=" + XmlParser.INSTANCE.toXml(XmlParser.INSTANCE
        .writeMessageToDocument(newMap, null));
    assertEquals(RequestParsingImplTest.getMessage(result, correctResult), result, correctResult);
    testFile.delete();
  }

  @Test
  public void addedToGroup() {
    String request = ADDED_TO_GROUP.getResponseNumber() + "=test_chat";
    String result = responseGenerating.responseGenerate(request);
    GroupMapImpl thisUser = new GroupMapImpl();
    thisUser.addUser("test_chat", CURRENT_USER.setCategory(CategoryUsers.FRIEND));
    String correctResult = ADDED_TO_GROUP.getResponseNumber() + "=" + GroupMapParserImpl.getInstance().groupMapToJSonString(thisUser);
    assertEquals(RequestParsingImplTest.getMessage(result, correctResult), result, correctResult);
  }
}