package ua.sumdu.java.lab2.messenger.handler.test;

import static ua.sumdu.java.lab2.messenger.handler.entities.RequestType.*;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import ua.sumdu.java.lab2.messenger.api.GroupMap;
import ua.sumdu.java.lab2.messenger.api.UserMap;
import ua.sumdu.java.lab2.messenger.entities.*;
import ua.sumdu.java.lab2.messenger.handler.processing.AddingRequestParsing;
import ua.sumdu.java.lab2.messenger.handler.processing.MessageRequestGeneratingImpl;
import ua.sumdu.java.lab2.messenger.handler.processing.RequestGeneratingImpl;
import ua.sumdu.java.lab2.messenger.handler.processing.RequestParsingImpl;
import ua.sumdu.java.lab2.messenger.parsers.XmlParser;
import ua.sumdu.java.lab2.messenger.processing.GroupMapParserImpl;
import ua.sumdu.java.lab2.messenger.processing.UserMapParserImpl;

@RunWith(DataProviderRunner.class)
@PrepareForTest({AddingRequestParsing.class})
public class RequestParsingImplTest {

    private RequestGeneratingImpl requestGenerating;
    private final RequestParsingImpl requestParsing = new RequestParsingImpl();
    private static final User TEST_USER = new User(CategoryUsers.BLACKLIST, "test_user", "test_user@ex.so",
    8080, User.getCurrentUser().getIpAddress());

    /**
     * Return test group.
     */
    @DataProvider
    public static Object[][] groupForTest() throws UnknownHostException {
        User[] users = {new User(CategoryUsers.FRIEND, "user1", "user1@ex.so", 8080,
        InetAddress.getLocalHost()), new User(CategoryUsers.BLACKLIST, "user2", "user2@ex.so",
            8080, InetAddress.getLocalHost()), new User(CategoryUsers.BLACKLIST, "user3", "user3@ex.so",
            8080, InetAddress.getLocalHost()), new User(CategoryUsers.BLACKLIST, "user4", "user4@ex.so",
            8080, InetAddress.getLocalHost()), new User(CategoryUsers.BLACKLIST, "user5", "user5@ex.so",
            8080, InetAddress.getLocalHost())};
        UserMapImpl userMap = new UserMapImpl();
        String nameChat = "test1";
        for (User user : users) {
            userMap.addUser(user);
        }
        return new Object[][]{{nameChat, userMap}};
    }

    @DataProvider
    public static Object[][] messages() throws UnknownHostException {
        User currentUser = User.getCurrentUser();
        String testUsername = "user1";
        return new Object[][] {{new Message(testUsername, currentUser.getUsername(), "text1",
            LocalDateTime.now()), new Message(testUsername, currentUser.getUsername(), "text2",
            LocalDateTime.now()), new Message(testUsername, currentUser.getUsername(), "text3",
            LocalDateTime.now()), new Message(testUsername, currentUser.getUsername(), "text4",
            LocalDateTime.now())}};
    }

    /**
     * The method initializes variables before testing.
     */
    @Before
    public void init() throws UnknownHostException {
        AddingRequestParsing.test = true;
        requestGenerating = new RequestGeneratingImpl();
        UserMapImpl userMap = (UserMapImpl) UserMapParserImpl.getInstance().getFriends();
        userMap.addUser(TEST_USER);
        UserMapParserImpl.getInstance().writeUserMapToFile(UserMapParserImpl.getInstance()
            .userMapToJSonString(userMap));
    }

    /**
     * delete test data.
     */
    @After
    public void after() {
        UserMapImpl userMap = (UserMapImpl) UserMapParserImpl.getInstance().getFriends();
        userMap.removeUser(TEST_USER);
        UserMapParserImpl.getInstance().writeUserMapToFile(UserMapParserImpl.getInstance()
            .userMapToJSonString(userMap));
    }

    @Test
    public void addToFriends() {
        UserMapParserImpl userMapParser = UserMapParserImpl.getInstance();
        UserMapImpl userMap = (UserMapImpl) userMapParser.getFriends();
        userMap.addUser(User.getCurrentUser().setCategory(CategoryUsers.FRIEND));
        String request = requestGenerating.creatingFriendsRequest();
        requestParsing.requestParsing(request);
        UserMapImpl newMap = (UserMapImpl) userMapParser.getFriends();
        Assert.assertEquals(getMessage(newMap.toString(), userMap.toString()), newMap, userMap);
    }

    @UseDataProvider("groupForTest")
    @Test
    @PrepareForTest({AddingRequestParsing.class})
    public void addGroup(String chatName, UserMap userMap) {
        GroupMapParserImpl groupMapParser = GroupMapParserImpl.getInstance();
        GroupMap groupMap = groupMapParser.getGroupMap();
        GroupMap groupForRequest = new GroupMapImpl();
        for (User user : userMap.getMap().values()) {
            groupForRequest.addUser(chatName, user);
        }
        String request = ADD_TO_GROUP.getRequestNumber() + "="
            + groupMapParser.groupMapToJSonString(groupForRequest);
        requestParsing.requestParsing(request);
        GroupMapImpl newGroups = (GroupMapImpl) groupMapParser.getGroupMap();
        groupMap.getMap().put(chatName, userMap);
        Assert.assertEquals(getMessage(newGroups.toString(),    groupMap.toString()),
            newGroups, groupMap);
        groupMap.getMap().remove(chatName);
        groupMapParser.writeGroupMapToFile(groupMapParser.groupMapToJSonString(groupMap));
    }

    @UseDataProvider("messages")
    @Test
    public void newMessage(Message mess) {
        UserMapImpl friends = (UserMapImpl) UserMapParserImpl.getInstance().getFriends();
        User user = User.getCurrentUser();
        String request = new MessageRequestGeneratingImpl().createRequestForNewMessage(mess);
        String sender = mess.getSender();
        user = user.setCategory(CategoryUsers.FRIEND).setUsername(sender);
        friends.addUser(user);
        UserMapParserImpl.getInstance().writeUserMapToFile(UserMapParserImpl.getInstance()
            .userMapToJSonString(friends));
        File messageFile = new File(User.getUrlMessageDirectory() + "/" + sender + ".xml");
        MessageMapImpl messageMap = (MessageMapImpl) XmlParser.INSTANCE.read(messageFile);
        messageMap.addMessage(mess);
        requestParsing.requestParsing(request);
        MessageMapImpl newMap = (MessageMapImpl) XmlParser.INSTANCE.read(messageFile);
        Assert.assertEquals(getMessage(newMap.getMapForMails().toString(),    messageMap.getMapForMails()
            .toString()), newMap, messageMap);
        messageMap.deleteMessage(mess);
        XmlParser.INSTANCE.write(messageMap, messageFile);
        friends.removeUser(user);
        UserMapParserImpl.getInstance().writeUserMapToFile(UserMapParserImpl.getInstance()
            .userMapToJSonString(friends));
    }

    public static String getMessage(String str1, String str2) {
    return "uncorrected result: <" + str1 + ">, but should be <" + str2 + ">";
    }
}