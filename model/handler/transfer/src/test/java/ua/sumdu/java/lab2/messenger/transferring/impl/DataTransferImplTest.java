package ua.sumdu.java.lab2.messenger.transferring.impl;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.powermock.api.mockito.PowerMockito.*;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import java.io.File;
import org.junit.Test;
import org.junit.runner.RunWith;
import ua.sumdu.java.lab2.messenger.entities.SentFiles;
import ua.sumdu.java.lab2.messenger.entities.User;

@RunWith(DataProviderRunner.class)
public class DataTransferImplTest {
    private DataTransferImpl dataTransfer;

    @DataProvider
    public static Object[][] data() {
        SentFiles files = new SentFiles();
        files.addFile(new File("./file1.xml"));
        files.addFile(new File("./file2.xml"));
        files.addFile(new File("./file2.xml"));
        return new Object[][]{{files}};
    }

    @Test
    @UseDataProvider("data")
    public void dataRequest(SentFiles files) {
        dataTransfer = new DataTransferImpl();
        assertEquals(files.toJSonString(), dataTransfer.dataRequest(files));
    }

    @Test
    @UseDataProvider("data")
    public void requestParsing(SentFiles files) {
        String result = files.toJSonString();
        dataTransfer = spy(new DataTransferImpl());
        doReturn(files).when(dataTransfer).userInteraction(any(), any());
        String response = User.getCurrentUser().getUsername() + "==" + files.toJSonString();
        assertEquals(result, dataTransfer.requestParsing(response));
    }

    @Test
    @UseDataProvider("data")
    public void dataAcquisition(SentFiles files) {
        dataTransfer = spy(new DataTransferImpl());
        int port = 1000;
        when(dataTransfer.getFreePort()).thenReturn(port);
        SendingAndReceivingFilesImpl sendingAndReceivingFiles = mock(SendingAndReceivingFilesImpl.class);
        when(dataTransfer.getSendingFilesElements()).thenReturn(sendingAndReceivingFiles);
        doNothing().when(sendingAndReceivingFiles).listenPort(anyInt(), anyObject());
        String result = String.valueOf(User.getCurrentUser().getIpAddress()).substring(1) + ":" + port + "==" + files.toJSonString();
        assertEquals(result, dataTransfer.dataAcquisition(files.toJSonString()));
    }

/*    @Test
    public void parsingDataSendingRejectedResponse() {
        String name = "test";
        dataTransfer = new DataTransferImpl();
        dataTransfer.parsingDataSendingRejectedResponse(name);
        MessageMapImpl messageMap = (MessageMapImpl) XmlParser.INSTANCE.
                read(User.getSystemMessageFile());
        boolean isFind = false;
        for (Message message : messageMap.getMapForMails().values()) {
            if (("User " + name + " declined to receive files").equals(message.getText())
                    && LocalDateTime.now().minusSeconds(1).isBefore(message.getTimeSending())) {
                isFind = true;
                messageMap.deleteMessage(message);
            }
        }
        Assert.assertTrue(isFind);
        XmlParser.INSTANCE.write(messageMap, User.getSystemMessageFile());
    }*/

}