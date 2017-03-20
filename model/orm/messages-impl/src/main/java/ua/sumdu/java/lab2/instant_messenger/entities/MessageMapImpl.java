package ua.sumdu.java.lab2.instant_messenger.entities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.java.lab2.instant_messenger.api.MessageMap;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class MessageMapImpl implements MessageMap {

    private static final Logger LOG = LoggerFactory.getLogger(MessageMapImpl.class);
    private Map<LocalDateTime, String> mapForMails;

    public Map<LocalDateTime, String> getMapForMails() {
        return mapForMails;
    }

    public void setMapForMails(Map<LocalDateTime, String> mapForMails) {
        this.mapForMails = mapForMails;
    }


    public MessageMapImpl() {
        mapForMails = new TreeMap<>();
    }

    public MessageMapImpl(Map<LocalDateTime, String> mapForMails) {

        this.mapForMails = mapForMails;
    }

    @Override
    public void addMessage(Message message) {
        LOG.debug("Add message");
    }

    @Override
    public void deleteMessage(Message message) {
        LOG.debug("Delete message");
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
}
