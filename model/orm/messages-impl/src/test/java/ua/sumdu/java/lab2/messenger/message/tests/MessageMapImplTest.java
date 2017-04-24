package ua.sumdu.java.lab2.messenger.message.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ua.sumdu.java.lab2.messenger.entities.CategoryUsers.BLACKLIST;
import static ua.sumdu.java.lab2.messenger.entities.CategoryUsers.FRIEND;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import ua.sumdu.java.lab2.messenger.entities.Message;
import ua.sumdu.java.lab2.messenger.entities.MessageMapImpl;
import ua.sumdu.java.lab2.messenger.entities.User;

@RunWith(DataProviderRunner.class)
public class MessageMapImplTest {

    private static final MessageMapImpl CORRECT_MAP = new MessageMapImpl();
    private static final MessageMapImpl MESSAGE_MAP = new MessageMapImpl();

    @DataProvider
    public static Object[][] data() throws UnknownHostException {
        User[] users = {new User(FRIEND, "user1", "user1@ex.so", 8080, InetAddress.getLocalHost()),
            new User(BLACKLIST, "user2", "user2@ex.so", 8080, InetAddress.getLocalHost()),
            new User(BLACKLIST, "user3", "user3@ex.so", 8080, InetAddress.getLocalHost()),
            new User(BLACKLIST, "user4", "user4@ex.so", 8080, InetAddress.getLocalHost()),
            new User(BLACKLIST, "user5", "user5@ex.so", 8080, InetAddress.getLocalHost())};
        return new Message[][]{{new Message(users[0].getUsername(), users[1].getUsername(), "text1",
            LocalDateTime.now()), new Message(users[1].getUsername(), users[2].getUsername(), "text2",
            LocalDateTime.now()), new Message(users[2].getUsername(), users[3].getUsername(), "text3",
            LocalDateTime.now()), new Message(users[3].getUsername(), users[4].getUsername(), "text4",
            LocalDateTime.now())}};
    }

    @Test
    @UseDataProvider("data")
    public void addMessage(Message message) throws IOException {
        CORRECT_MAP.getMapForMails().put(message.getTimeSending(), message);
        MESSAGE_MAP.addMessage(message);
        assertTrue(CORRECT_MAP.equals(MESSAGE_MAP));
    }

    @DataProvider
    public static Object[][] dataForDelete() throws UnknownHostException {
        Message[] messages = (Message[])data()[0];
        return new Object[][]{{messages[0], messages[messages.length - 1], messages[3]}};
    }

    @Test
    @UseDataProvider("dataForDelete")
    public void deleteMessage(Message message) throws IOException {
        MESSAGE_MAP.deleteMessage(message);
        CORRECT_MAP.getMapForMails().remove(message.getTimeSending());
        assertTrue(CORRECT_MAP.equals(MESSAGE_MAP));
    }

    @Test
    public void getetAndSetterTest() {
        Message mess = new Message("user1", "user2", "text1",
                LocalDateTime.now());
        Map<LocalDateTime, Message> map = new TreeMap<>();
        map.put(mess.getTimeSending(), mess);
        MessageMapImpl messageMap = new MessageMapImpl();
        messageMap.setMapForMails(map);
        assertEquals(map, messageMap.getMapForMails());
    }
}