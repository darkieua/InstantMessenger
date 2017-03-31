package ua.sumdu.java.lab2.instant_messenger.message_impl.tests;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import ua.sumdu.java.lab2.instant_messenger.entities.Message;
import ua.sumdu.java.lab2.instant_messenger.entities.MessageMapImpl;
import ua.sumdu.java.lab2.instant_messenger.entities.User;
import ua.sumdu.java.lab2.instant_messenger.parsers.XMLParser;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalDateTime;

import static org.junit.Assert.assertTrue;
import static ua.sumdu.java.lab2.instant_messenger.entities.CategoryUsers.BLACKLIST;
import static ua.sumdu.java.lab2.instant_messenger.entities.CategoryUsers.FRIEND;


@RunWith(DataProviderRunner.class)
public class XMLParserTest {

    @DataProvider
    public static Object[][] data() throws IOException {
        User[] users = {new User(FRIEND, "user1", "user1@ex.so", 8080, InetAddress.getLocalHost()),
                new User(BLACKLIST, "user2", "user2@ex.so", 8080, InetAddress.getLocalHost()),
                new User(BLACKLIST, "user3", "user3@ex.so", 8080, InetAddress.getLocalHost()),
                new User(BLACKLIST, "user4", "user4@ex.so", 8080, InetAddress.getLocalHost()),
                new User(BLACKLIST, "user5", "user5@ex.so", 8080, InetAddress.getLocalHost())};
        Message[] messages = {new Message(users[0].getUsername(), users[1].getUsername(), "text1", LocalDateTime.now()),
                new Message(users[1].getUsername(), users[2].getUsername(), "text2", LocalDateTime.now()),
                new Message(users[2].getUsername(), users[3].getUsername(), "text3", LocalDateTime.now()),
                new Message(users[3].getUsername(), users[4].getUsername(), "text4", LocalDateTime.now())};
        MessageMapImpl map = new MessageMapImpl();
        for (Message mess : messages) {
            map.addMessage(mess);
        }return new Object[][]{{map}};
    }

    @Test
    @UseDataProvider("data")
    public void writeAndReadFile(MessageMapImpl map) throws IOException {
        File file = File.createTempFile("file","test");
        XMLParser.INSTANCE.write(map, file);
        MessageMapImpl newMap = (MessageMapImpl) XMLParser.INSTANCE.read(file);
        assertTrue(map.equals(newMap));
        file.deleteOnExit();
    }
}