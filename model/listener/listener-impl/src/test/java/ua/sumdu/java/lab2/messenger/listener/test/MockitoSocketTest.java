package ua.sumdu.java.lab2.messenger.listener.test;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.mockito.Mockito.when;
import static ua.sumdu.java.lab2.messenger.entities.User.CURRENT_USER;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ua.sumdu.java.lab2.messenger.listener.impl.*;
import ua.sumdu.java.lab2.messenger.entities.*;
import ua.sumdu.java.lab2.messenger.handler.entities.*;
import ua.sumdu.java.lab2.messenger.handler.processing.RequestGeneratingImpl;
import ua.sumdu.java.lab2.messenger.parsers.XmlParser;
import ua.sumdu.java.lab2.messenger.processing.UserCreatorImpl;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ClientImpl.class, MultiThreadedServerImpl.class})
public class MockitoSocketTest {

  @Test
  public void userIsOfflineTest() throws UnknownHostException {
    RequestGeneratingImpl requestGenerating = new RequestGeneratingImpl();
    String request = requestGenerating.addToFriends();
    LocalDateTime date = LocalDateTime.now();
    InetAddress thisIp = InetAddress.getByName("localhost");
    ClientImpl client = spy(new ClientImpl(thisIp, 8048, request));
    when(client.socketInit(thisIp, 8048)).thenReturn(false);
    client.run();
    MessageMapImpl messageMap = (MessageMapImpl) XmlParser.INSTANCE.read(User.getSystemMessageFile());
    boolean isFind = false;
    for (Message message : messageMap.getMapForMails().values()) {
      if (("User(" + thisIp.getHostAddress() + ") is offline").equals(message.getText())
          && date.isBefore(message.getTimeSending())) {
        isFind = true;
        messageMap.deleteMessage(message);
      }
    }
    assertTrue(isFind);
  }

  @Test
  public void correctWork() throws IOException {
    User newUser = UserCreatorImpl.INSTANCE.createUser(CategoryUsers.FRIEND, "test_user", "test_user@go.com",
        InetAddress.getLocalHost(), 8048);
    String request = RequestType.ADD_TO_FRIENDS.getRequestNumber() + "=" + newUser.toJSonString();
    // initialize server
    MultiThreadedServerImpl multiThreadedServer = spy(new MultiThreadedServerImpl());
    ServerSocket server = mock(ServerSocket.class);
    doReturn(server).when(multiThreadedServer).startServet();
    multiThreadedServer.setTest(true);
    multiThreadedServer.start();
    // initialise sockets
    Socket connectedClient = mock(Socket.class);
    // initialize streams
    InputStream ccis = new ByteArrayInputStream(request.getBytes());
    PipedOutputStream ccos = new PipedOutputStream();
    PipedInputStream mcis = new PipedInputStream(ccos);
    when(connectedClient.getOutputStream()).thenReturn(ccos);
    when(connectedClient.getInputStream()).thenReturn(ccis);
    // initialise connection
    when(server.accept()).thenReturn(connectedClient);
    BufferedReader in = new BufferedReader(new InputStreamReader(mcis));
    String response = "";
    LineIterator iterator = IOUtils.lineIterator(in);
    while (iterator.hasNext()) {
      response = response + iterator.nextLine() + "\n";
    }
    assertEquals(response, ResponseType.ADDED_TO_FRIENDS.getResponseNumber()
        + "=" + CURRENT_USER.setCategory(CategoryUsers.FRIEND).toJSonString() + "\n");
  }

}
