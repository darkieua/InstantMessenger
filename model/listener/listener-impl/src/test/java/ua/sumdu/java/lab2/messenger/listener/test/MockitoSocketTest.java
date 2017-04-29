package ua.sumdu.java.lab2.messenger.listener.test;

import static org.junit.Assert.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ua.sumdu.java.lab2.messenger.listener.impl.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ClientImpl.class, MultiThreadedServerImpl.class})
public class MockitoSocketTest {

/*    @Test
    public void userIsOfflineTest() throws UnknownHostException {
        RequestGeneratingImpl requestGenerating = new RequestGeneratingImpl();
        String request = requestGenerating.creatingFriendsRequest();
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
    }*/

    /*@Test
    public void correctWork() throws IOException {
        User newUser = UserCreatorImpl.INSTANCE.createUser(CategoryUsers.FRIEND, "test_user", "test_user@go.com",
                InetAddress.getLocalHost(), 8048);
        String request = RequestType.ADD_TO_FRIENDS.getRequestNumber() + "=" + newUser.toJSonString();
        MultiThreadedServerImpl multiThreadedServer = spy(new MultiThreadedServerImpl());
        ServerSocket server = mock(ServerSocket.class);
        doReturn(server).when(multiThreadedServer).startServet();
        multiThreadedServer.start();
        Socket connectedClient = mock(Socket.class);
        InputStream ccis = new ByteArrayInputStream(request.getBytes());
        PipedOutputStream ccos = new PipedOutputStream();
        PipedInputStream mcis = new PipedInputStream(ccos);
        when(connectedClient.getOutputStream()).thenReturn(ccos);
        when(connectedClient.getInputStream()).thenReturn(ccis);
        when(server.accept()).thenReturn(connectedClient);
        BufferedReader in = new BufferedReader(new InputStreamReader(mcis));
        String response = "";
        LineIterator iterator = IOUtils.lineIterator(in);
        while (iterator.hasNext()) {
            response = response + iterator.nextLine() + "\n";
        }
        assertEquals(response, ResponseType.ADDED_TO_FRIENDS.getResponseNumber()
                + "=" + User.getCurrentUser().setCategory(CategoryUsers.FRIEND).toJSonString() + "\n");
    }*/

    @Test
    public void getExecutor() {
        MultiThreadedServerImpl multiThreadedServer = new MultiThreadedServerImpl();
        ExecutorService service = Executors.newCachedThreadPool();
        multiThreadedServer.setService(service);
        assertEquals(multiThreadedServer.getService(),    multiThreadedServer.getService());
    }
}
