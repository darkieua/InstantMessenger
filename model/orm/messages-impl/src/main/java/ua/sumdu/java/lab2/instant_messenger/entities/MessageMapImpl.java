package ua.sumdu.java.lab2.instant_messenger.entities;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.java.lab2.instant_messenger.api.MessageMap;

import java.io.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MessageMapImpl implements MessageMap {

    private static final Logger LOG = LoggerFactory.getLogger(MessageMapImpl.class);
    private Map<LocalDateTime, String> mapForMails;

    public Map<LocalDateTime, String> getMapForMails() {
        return mapForMails;
    }

    public MessageMapImpl setMapForMails(Map<LocalDateTime, String> mapForMails) {
        this.mapForMails = mapForMails;
        return this;
    }


    public MessageMapImpl() {
        mapForMails = new HashMap<>();
    }

    public MessageMapImpl(Map<LocalDateTime, String> mapForMails) {
        this.mapForMails = mapForMails;
    }

    @Override
    public void addMessage(Message message) throws IOException {
        LOG.debug("Add message");
        String str = getTextView(message);
        System.out.println(str);
        mapForMails.put(message.getTimeSending(), str);
    }

    @Override
    public void deleteMessage(Message message) throws IOException {
        LOG.debug("Delete message");
        mapForMails.remove(message.getTimeSending(), getTextView(message));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MessageMapImpl that = (MessageMapImpl) obj;
        return Objects.equals(mapForMails, that.mapForMails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mapForMails);
    }

    private String getTextView(Message message) throws IOException {
        File tempFile = File.createTempFile("message"+message.getTest(), "temp");
        ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(tempFile));
        oos.writeObject(message);
        DataInputStream dis = new DataInputStream(
                new FileInputStream(tempFile));
        final byte[] bytes = new byte[dis.available()];
        dis.readFully(bytes);
        tempFile.delete();
        return new String(bytes, 0, bytes.length);
    }
}
