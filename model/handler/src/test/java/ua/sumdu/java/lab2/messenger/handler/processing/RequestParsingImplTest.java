package ua.sumdu.java.lab2.messenger.handler.processing;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import ua.sumdu.java.lab2.messenger.entities.*;
import ua.sumdu.java.lab2.messenger.parsers.XmlParser;
import ua.sumdu.java.lab2.messenger.processing.GroupMapParserImpl;
import ua.sumdu.java.lab2.messenger.processing.UserMapParserImpl;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

import static ua.sumdu.java.lab2.messenger.entities.CategoryUsers.*;
import static ua.sumdu.java.lab2.messenger.handler.entities.RequestType.*;
import static ua.sumdu.java.lab2.messenger.handler.entities.ResponseType.REQUESTED_MESSAGES;
import static ua.sumdu.java.lab2.messenger.handler.entities.ResponseType.UPDATED_GROUP_LIST;


@RunWith(DataProviderRunner.class)
public class RequestParsingImplTest {

    RequestGeneratingImpl requestGenerating;
    RequestParsingImpl requestParsing;

    @DataProvider
    public static Object[][] groupForTest() throws UnknownHostException {
        User[] users = {new User(FRIEND, "user1", "user1@ex.so", 8080, InetAddress.getLocalHost()),
                new User(BLACKLIST, "user2", "user2@ex.so", 8080, InetAddress.getLocalHost()),
                new User(BLACKLIST, "user3", "user3@ex.so", 8080, InetAddress.getLocalHost()),
                new User(BLACKLIST, "user4", "user4@ex.so", 8080, InetAddress.getLocalHost()),
                new User(BLACKLIST, "user5", "user5@ex.so", 8080, InetAddress.getLocalHost())};
        UserMapImpl userMap = new UserMapImpl();
        String nameChat = "test1";
        for (User user : users) {
            userMap.addUser(user);
        }
        return new Object[][]{{nameChat, userMap}};
    }

    @DataProvider
    public static Object[][] messages() throws UnknownHostException {
        User[] users = {new User(FRIEND, "user1", "user1@ex.so", 8080, InetAddress.getLocalHost()),
                new User(ADMIN, "user2", "user2@ex.so", 8080, InetAddress.getLocalHost()),
                new User(VISITOR, "user3", "user3@ex.so", 8080, InetAddress.getLocalHost()),
                new User(FRIEND, "user4", "user4@ex.so", 8080, InetAddress.getLocalHost()),
                new User(FRIEND, "user5", "user5@ex.so", 8080, InetAddress.getLocalHost())};
        return new Object[][] {{new Message(users[0].getUsername(), users[1].getUsername(), "text1", LocalDateTime.now()),
                new Message(users[1].getUsername(), users[2].getUsername(), "text2", LocalDateTime.now()),
                new Message(users[2].getUsername(), users[3].getUsername(), "text3", LocalDateTime.now()),
                new Message(users[3].getUsername(), users[4].getUsername(), "text4", LocalDateTime.now())}};
    }

    @Before
    public void init() {
        requestParsing = new RequestParsingImpl();
        requestGenerating = new RequestGeneratingImpl();
        requestParsing.setTest(true);
    }

    @Test
    public void addToFriends() {
        UserMapParserImpl userMapParser = UserMapParserImpl.getInstance();
        UserMapImpl userMap = (UserMapImpl) userMapParser.getFriends();
        userMap.addUser(User.CURRENT_USER);
        String request = requestGenerating.addToFriends();
        requestParsing.requestParser(request);
        UserMapImpl newMap = (UserMapImpl) userMapParser.getFriends();
        Assert.assertEquals(getMessage(newMap.toString(), userMap.toString()), newMap, userMap);
    }

    @UseDataProvider("groupForTest")
    @Test
    public void addGroup(String chatName, UserMapImpl userMap) {
        GroupMapParserImpl groupMapParser = GroupMapParserImpl.getInstance();
        GroupMapImpl groupMap = (GroupMapImpl) groupMapParser.getGroupMap();
        GroupMapImpl groupForRequest = new GroupMapImpl();
        groupForRequest.getMap().put(chatName, userMap);
        String request = ADD_TO_GROUP.getRequestNumber()+"="+groupMapParser.groupMapToJSonString(groupForRequest);
        requestParsing.requestParser(request);
        GroupMapImpl newGroups = (GroupMapImpl) groupMapParser.getGroupMap();
        groupMap.getMap().put(chatName, userMap);
        Assert.assertEquals(getMessage(newGroups.toString(),  groupMap.toString()), newGroups, groupMap);
        groupMap.getMap().remove(chatName);
        groupMapParser.writeGroupMapToFile(groupMapParser.groupMapToJSonString(groupMap));
    }

    @UseDataProvider("messages")
    @Test
    public void newMessage(Message mess) {
        String request = requestGenerating.newMessage(mess);
        String sender = mess.getSender();
        File messageFile = new File(User.getUrlMessageDirectory() + sender + ".xml");
        MessageMapImpl messageMap = (MessageMapImpl) XmlParser.INSTANCE.read(messageFile);
        messageMap.addMessage(mess);
        requestParsing.requestParser(request);
        MessageMapImpl newMap = (MessageMapImpl) XmlParser.INSTANCE.read(messageFile);
        Assert.assertEquals(getMessage(newMap.toString(),  messageMap.toString()), newMap, messageMap);
        messageMap.deleteMessage(mess);
        XmlParser.INSTANCE.write(messageMap, messageFile);
    }

    @UseDataProvider("messages")
    @Test
    public void newMessageToGroup(Message mess) {
        mess.setReceiver("testGroup");
        String request = requestGenerating.newMessageToGroup(mess);
        String receiver = mess.getReceiver();
        String path = User.getUrlMessageDirectory() + receiver + ".xml";
        MessageMapImpl messageMap = (MessageMapImpl) XmlParser.INSTANCE.read(new File(path));
        messageMap.addMessage(mess);
        XmlParser.INSTANCE.write(messageMap, new File(path));
        requestParsing.requestParser(request);
        MessageMapImpl newMap = (MessageMapImpl) XmlParser.INSTANCE.read(new File(path));
        Assert.assertEquals(getMessage(newMap.toString(),  messageMap.toString()), newMap, messageMap);
        messageMap.deleteMessage(mess);
        XmlParser.INSTANCE.write(messageMap, new File(path));
    }

    @Test
    public void updateGroupList() throws UnknownHostException {
        User user = new User(FRIEND, "tempUser", "tempUser@ex.com", 8020, InetAddress.getLocalHost());
        GroupMapParserImpl groupMapParser = GroupMapParserImpl.getInstance();
        String chatName = "main";
        GroupMapImpl allGroups = (GroupMapImpl) groupMapParser.getGroupMap();
        GroupMapImpl groupMap = new GroupMapImpl();
        groupMap.getMap().put(chatName, allGroups.getMap().get(chatName));
        groupMap.addUser(chatName, user);
        allGroups.addUser(chatName, user);
        String request = UPDATE_GROUP_LIST.getRequestNumber() + "=" + groupMapParser.groupMapToJSonString(groupMap);
        requestParsing.requestParser(request);
        GroupMapImpl newGroups = (GroupMapImpl) groupMapParser.getGroupMap();
        Assert.assertEquals(getMessage(newGroups.toString(), allGroups.toString()), newGroups, allGroups);
        groupMap.getMap().get(chatName).removeUser(user);
        groupMapParser.writeGroupMapToFile(groupMapParser.groupMapToJSonString(groupMap));
    }

    @Test
    public void requestForUpdateLists() {
        String request1 = REQUEST_FOR_UPDATE_GROUP_LIST.getRequestNumber() + "=" + "main";
        String response1 = UPDATED_GROUP_LIST.getResponseNumber() + " " + "main";
        String newResponse1 = requestParsing.requestParser(request1);
        Assert.assertEquals(getMessage(newResponse1, response1), newResponse1, response1);
        String request2 = MESSAGES_FROM_A_SPECIFIC_DATE.getRequestNumber() + "=" + 1;
        String response2 = REQUESTED_MESSAGES.getResponseNumber() + " " + 1;
        String newResponse2 = requestParsing.requestParser(request2);
        Assert.assertEquals(getMessage(newResponse2, response2), newResponse2, response2);
    }

    public static String getMessage(String str1, String str2) {
        return "uncorrected result: <" + str1 + ">, but should be <" + str2 + ">";
    }

  @Test
  public void newBlackListMessage() throws UnknownHostException {
    User user1 = new User(BLACKLIST, "user1", "user1@ex.so", 8080, InetAddress.getLocalHost());
    Message mess = new Message(user1.getUsername(), User.CURRENT_USER.getUsername(), "text", LocalDateTime.now());
    String request = requestGenerating.newMessage(mess);
    String sender = mess.getSender();
    File messageFile = new File(User.getUrlMessageDirectory() + sender + ".xml");
    MessageMapImpl messageMap = (MessageMapImpl) XmlParser.INSTANCE.read(messageFile);
    requestParsing.requestParser(request);
    MessageMapImpl newMap = (MessageMapImpl) XmlParser.INSTANCE.read(messageFile);
    Assert.assertEquals(getMessage(newMap.toString(),  messageMap.toString()), newMap, messageMap);
    messageMap.deleteMessage(mess);
    XmlParser.INSTANCE.write(messageMap, messageFile);
  }
}