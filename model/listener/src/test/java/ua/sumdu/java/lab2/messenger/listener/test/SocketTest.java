package ua.sumdu.java.lab2.messenger.listener.test;

import static org.junit.Assert.assertTrue;
import static ua.sumdu.java.lab2.messenger.entities.CategoryUsers.FRIEND;
import static ua.sumdu.java.lab2.messenger.entities.User.CURRENT_USER;
import static ua.sumdu.java.lab2.messenger.handler.entities.RequestType.ADD_TO_FRIENDS;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import org.junit.Test;
import ua.sumdu.java.lab2.messenger.entities.Message;
import ua.sumdu.java.lab2.messenger.entities.MessageMapImpl;
import ua.sumdu.java.lab2.messenger.entities.User;
import ua.sumdu.java.lab2.messenger.entities.UserMapImpl;
import ua.sumdu.java.lab2.messenger.handler.processing.RequestGeneratingImpl;
import ua.sumdu.java.lab2.messenger.listener.impl.ClientImpl;
import ua.sumdu.java.lab2.messenger.listener.impl.MultiThreadedServerImpl;
import ua.sumdu.java.lab2.messenger.parsers.XmlParser;
import ua.sumdu.java.lab2.messenger.processing.UserCreatorImpl;
import ua.sumdu.java.lab2.messenger.processing.UserMapParserImpl;

public class SocketTest {

  @Test
  public void userIsOfflineTest() throws UnknownHostException {
    RequestGeneratingImpl requestGenerating = new RequestGeneratingImpl();
    String request = requestGenerating.addToFriends();
    InetAddress thisIp = InetAddress.getByName("localhost");
    ClientImpl client = new ClientImpl(thisIp, 8048, request);
    client.run();
    MessageMapImpl messageMap = (MessageMapImpl) XmlParser.INSTANCE.read(User.getSystemMessageFile());
    boolean isFind = false;
    for (Message message : messageMap.getMapForMails().values()) {
      if (("User(" + thisIp.getHostAddress() + ") is offline").equals(message.getText())
          && LocalDateTime.now().minusSeconds(1).isBefore(message.getTimeSending())) {
        isFind = true;
        messageMap.deleteMessage(message);
      }
    }
    assertTrue(isFind);
  }

  @Test
  public void correctWork() throws UnknownHostException {
    MultiThreadedServerImpl multiThreadedServer = new MultiThreadedServerImpl();
    multiThreadedServer.setTest(true);
    multiThreadedServer.start();
    InetAddress thisIp = InetAddress.getByName("localhost");
    User newUser = UserCreatorImpl.INSTANCE.createUser(FRIEND, "test_user", "test_user@go.com",
        InetAddress.getLocalHost(), 8048);
    String request = ADD_TO_FRIENDS.getRequestNumber() + "=" + newUser.toJSonString();
    ClientImpl client = new ClientImpl(thisIp, CURRENT_USER.getPort(), request);
    client.run();
    UserMapImpl userMap = (UserMapImpl) UserMapParserImpl.getInstance().getFriends();
    boolean isFind = false;
    for (User user : userMap.getMap().values()) {
      if (user.equals(newUser)) {
        userMap.removeUser(user);
        isFind = true;
      }
    }
    assertTrue(isFind);
    UserMapParserImpl.getInstance().writeUserMapToFile(UserMapParserImpl.getInstance().userMapToJSonString(userMap));
  }
}
