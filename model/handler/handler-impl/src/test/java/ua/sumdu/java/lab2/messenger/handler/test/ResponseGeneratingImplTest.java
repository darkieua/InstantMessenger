package ua.sumdu.java.lab2.messenger.handler.test;

import static ua.sumdu.java.lab2.messenger.handler.entities.ResponseType.*;

import com.tngtech.java.junit.dataprovider.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import org.junit.*;
import org.junit.runner.RunWith;
import ua.sumdu.java.lab2.messenger.entities.*;
import ua.sumdu.java.lab2.messenger.handler.processing.ResponseGeneratingImpl;
import ua.sumdu.java.lab2.messenger.parsers.XmlParser;
import ua.sumdu.java.lab2.messenger.processing.GroupMapParserImpl;
import ua.sumdu.java.lab2.messenger.processing.SettingsImpl;
import ua.sumdu.java.lab2.messenger.processing.SettingsParserImpl;

@RunWith(DataProviderRunner.class)
public class ResponseGeneratingImplTest {

    ResponseGeneratingImpl responseGenerating;
    private static String testUser = "test_user";
    private boolean isWrite = false;

    @Before
    public void init() throws IOException {
        responseGenerating = new ResponseGeneratingImpl();
        File file = new File(User.getUserConfigPath());
        if (!file.exists()) {
            isWrite = true;
            file.createNewFile();
            SettingsImpl settings = new SettingsImpl();
            try {
                String ipAddress = InetAddress.getLocalHost()
                        .getHostAddress();
                settings.putSetting("ipAddress", ipAddress);
            } catch (UnknownHostException e) {
                settings.putSetting("ipAddress", "");
            }
            settings.putSetting("port", String.valueOf(9696));
            settings.putSetting("downloadPath", "");
            settings.putSetting("username", "testUser");
            settings.putSetting("email", "test@ex.co");
            Date out = Date.from(LocalDateTime.now()
                    .atZone(ZoneId.systemDefault())
                    .toInstant());
            settings.putSetting("lastLoginTime",
                    String.valueOf(out.getTime()));
            SettingsParserImpl settingsParser = new SettingsParserImpl();
            String result = settingsParser.settingsToJson(settings);
            FileWriter writer = new FileWriter(file, false);
            writer.write(result);
            writer.flush();
            writer.close();
        }
    }

    @After
    public void after() {
        if (isWrite) {
            new File(User.getUserConfigPath()).delete();
        }
    }

    @DataProvider
    public static Object[][] shortResponsesWithoutParsing() {
        return new Object[][]{{String.valueOf(SUCCESSFUL.getResponseNumber())}, {String.valueOf(USER_IS_OFFLINE.getResponseNumber())}};
    }

    @DataProvider
    public static Object[][] tempGroup() throws UnknownHostException {
        User[] users = {new User(CategoryUsers.FRIEND, "user1", "user1@ex.so", 8080, InetAddress.getLocalHost()),
                new User(CategoryUsers.BLACKLIST, "user2", "user2@ex.so", 8080, InetAddress.getLocalHost()),
                new User(CategoryUsers.BLACKLIST, "user3", "user3@ex.so", 8080, InetAddress.getLocalHost()),
                new User(CategoryUsers.BLACKLIST, "user4", "user4@ex.so", 8080, InetAddress.getLocalHost()),
                new User(CategoryUsers.BLACKLIST, "user5", "user5@ex.so", 8080, InetAddress.getLocalHost())};
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
        Assert.assertEquals(RequestParsingImplTest.getMessage(result, request), result, request);
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
        Assert.assertEquals(RequestParsingImplTest.getMessage(result, correctResult), result, correctResult);
        currentGroups.getMap().remove(chatName);
        GroupMapParserImpl.getInstance().writeGroupMapToFile(GroupMapParserImpl.getInstance().groupMapToJSonString(currentGroups));
    }

    @DataProvider
    public static Object[][] messages() throws UnknownHostException {
        String username = "this_user";
        Message[] messages =    {new Message(testUser, username, "text1", LocalDateTime.now()),
            new Message(testUser, username, "text2", LocalDateTime.now().minusDays(1)),
            new Message(testUser, username, "text3", LocalDateTime.now().minusDays(2)),
            new Message(testUser, username, "text4", LocalDateTime.now().minusDays(3))};
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
        Assert.assertEquals(RequestParsingImplTest.getMessage(result, correctResult), result, correctResult);
        testFile.delete();
    }
}