package ua.sumdu.java.lab2.messenger.message.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ua.sumdu.java.lab2.messenger.entities.CategoryUsers.BLACKLIST;
import static ua.sumdu.java.lab2.messenger.entities.CategoryUsers.FRIEND;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalDateTime;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;
import ua.sumdu.java.lab2.messenger.entities.Message;
import ua.sumdu.java.lab2.messenger.entities.MessageMapImpl;
import ua.sumdu.java.lab2.messenger.entities.User;
import ua.sumdu.java.lab2.messenger.parsers.ParsingMessages;
import ua.sumdu.java.lab2.messenger.parsers.XmlParser;

@RunWith(DataProviderRunner.class)
public class XmlParserTest {

    @DataProvider
    public static Object[][] data() throws IOException {
        User[] users = {new User(FRIEND, "user1", "user1@ex.so", 8080, InetAddress.getLocalHost()),
            new User(BLACKLIST, "user2", "user2@ex.so", 8080, InetAddress.getLocalHost()),
            new User(BLACKLIST, "user3", "user3@ex.so", 8080, InetAddress.getLocalHost()),
            new User(BLACKLIST, "user4", "user4@ex.so", 8080, InetAddress.getLocalHost()),
            new User(BLACKLIST, "user5", "user5@ex.so", 8080, InetAddress.getLocalHost())};
        Message[] messages = {new Message(users[0].getUsername(), users[1].getUsername(), "text1",
            LocalDateTime.now()), new Message(users[1].getUsername(), users[2].getUsername(), "text2",
            LocalDateTime.now()), new Message(users[2].getUsername(), users[3].getUsername(), "text3",
            LocalDateTime.now()), new Message(users[3].getUsername(), users[4].getUsername(), "text4",
            LocalDateTime.now())};
        MessageMapImpl map = new MessageMapImpl();
        for (Message mess : messages) {
            map.addMessage(mess);
        }
        return new Object[][]{{map}};
    }

    @Test
    @UseDataProvider("data")
    public void writeAndReadFile(MessageMapImpl map) throws IOException {
        File file = File.createTempFile("file","test");
        XmlParser.INSTANCE.write(map, file);
        MessageMapImpl newMap = (MessageMapImpl) XmlParser.INSTANCE.read(file);
        assertTrue(map.equals(newMap));
        file.deleteOnExit();
    }

    @Test
    public void loadXmlFromString() throws IOException, ParserConfigurationException {
        Message firstMessage = new Message("user1", "user2", "text1",
                LocalDateTime.now());
        Document doc = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .getDOMImplementation()
                .createDocument(null, null, null);
        XmlParser.INSTANCE.addMessage(null, firstMessage, doc);
        String str = XmlParser.INSTANCE.toXml(doc);
        Document newDoc = XmlParser.loadXmlFromString(str);
        Message message = ParsingMessages.parseMessage(newDoc.getFirstChild());
        assertEquals(firstMessage, message);
    }
}