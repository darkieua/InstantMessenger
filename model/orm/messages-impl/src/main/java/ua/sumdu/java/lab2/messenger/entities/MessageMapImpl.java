package ua.sumdu.java.lab2.messenger.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.java.lab2.messenger.api.MessageMap;

public class MessageMapImpl implements MessageMap, Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(MessageMapImpl.class);
    private Map<LocalDateTime, Message> mapForMails = null;

    public MessageMapImpl() {
        mapForMails = new TreeMap<>();
    }

    public Map<LocalDateTime, Message> getMapForMails() {
        return mapForMails;
    }

    public MessageMapImpl setMapForMails(Map<LocalDateTime, Message> mapForMails) {
        this.mapForMails = mapForMails;
        return this;
    }

    public MessageMapImpl(Map<LocalDateTime, Message> mapForMails) {
        this.mapForMails = mapForMails;
    }

    @Override
    public String toString() {
        return "MessageMapImpl{" + "mapForMails=" + mapForMails + '}';
    }

    @Override
    public void addMessage(Message message) {
        LOG.debug("Add message");
        mapForMails.put(message.getTimeSending(), message);
    }

    @Override
    public void deleteMessage(Message message) {
        LOG.debug("Delete message");
        mapForMails.remove(message.getTimeSending(), message);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        MessageMapImpl that = (MessageMapImpl) obj;
        if (mapForMails.keySet().size() == that.mapForMails.size()) {
            for (LocalDateTime dateTime : mapForMails.keySet()) {
                if (!mapForMails.get(dateTime).equals(that.mapForMails.get(dateTime))) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(mapForMails);
    }

}
