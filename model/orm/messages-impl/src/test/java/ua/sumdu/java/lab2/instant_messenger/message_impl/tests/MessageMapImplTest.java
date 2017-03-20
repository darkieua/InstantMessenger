package ua.sumdu.java.lab2.instant_messenger.message_impl.tests;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import ua.sumdu.java.lab2.instant_messenger.entities.Message;
import ua.sumdu.java.lab2.instant_messenger.entities.MessageMapImpl;
import ua.sumdu.java.lab2.instant_messenger.entities.User;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

import static ua.sumdu.java.lab2.instant_messenger.entities.CategoryUsers.BLACKLIST;
import static ua.sumdu.java.lab2.instant_messenger.entities.CategoryUsers.FRIEND;
import static org.junit.Assert.assertTrue;

@RunWith(DataProviderRunner.class)
public class MessageMapImplTest {

    private static final MessageMapImpl correctMap = new MessageMapImpl();
    private static final MessageMapImpl messageMap = new MessageMapImpl();

    @DataProvider
    public static Object[][] data() throws UnknownHostException {
        User[] users = {new User(FRIEND, "user1", "user1@ex.so", 8080, InetAddress.getLocalHost()),
                new User(BLACKLIST, "user2", "user2@ex.so", 8080, InetAddress.getLocalHost()),
                new User(BLACKLIST, "user3", "user3@ex.so", 8080, InetAddress.getLocalHost()),
                new User(BLACKLIST, "user4", "user4@ex.so", 8080, InetAddress.getLocalHost()),
                new User(BLACKLIST, "user5", "user5@ex.so", 8080, InetAddress.getLocalHost())};
        Message[][] messages = {{new Message(users[0], users[1], "text1", LocalDateTime.now()),
                new Message(users[1], users[2], "text2", LocalDateTime.now()),
                new Message(users[2], users[3], "text3", LocalDateTime.now()),
                new Message(users[3], users[4], "text4", LocalDateTime.now())}};
        return messages;
    }

    @Test
    @UseDataProvider("data")
    public void addMessage(Message message) throws IOException {
        File tempFile = File.createTempFile("message"+message.getTest(), "temp");
        String str = "";
        final ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(tempFile));
        oos.writeObject(message);
        final DataInputStream dis = new DataInputStream(
                new FileInputStream(tempFile));
        final byte[] bytes = new byte[dis.available()];
        dis.readFully(bytes);
        str = new String(bytes, 0, bytes.length);
        correctMap.getMapForMails().put(message.getTimeSending(), str);
        messageMap.addMessage(message);
        assertTrue(correctMap.equals(messageMap));
        tempFile.deleteOnExit();
    }

    @DataProvider
    public static Object[][] dataForDelete() throws UnknownHostException {
        Message[] messages = (Message[])data()[0];
        Message[][] messagesForDeleting = {{messages[0], messages[messages.length-1], messages[3]}};
        return messagesForDeleting;
    }

    @Test
    @UseDataProvider("dataForDelete")
    public void deleteMessage(Message message) throws IOException {
        messageMap.deleteMessage(message);
        correctMap.getMapForMails().remove(message.getTimeSending());
        assertTrue(correctMap.equals(messageMap));
    }

}