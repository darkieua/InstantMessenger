package ua.sumdu.java.lab2.messenger.entities;

import java.time.LocalDateTime;
import java.util.Objects;

public class Message {

    private String sender;
    private String receiver;
    private String text;
    private LocalDateTime timeSending;

    /**
    * Message constructor.
    */

    public Message(String sender, String receiver, String text, LocalDateTime timeSending) {
        this.sender = sender;
        this.receiver = receiver;
        this.text = text;
        this.timeSending = timeSending;
    }

    @Override
    public String toString() {
        return "Message{" + "sender='" + sender + '\''
                + ", receiver='" + receiver + '\'' + ", text='"
                + text + '\'' + ", timeSending=" + timeSending+ '}';
    }


    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getTimeSending() {
        return timeSending;
    }

    public void setTimeSending(LocalDateTime timeSending) {
        this.timeSending = timeSending;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Message message = (Message) obj;
        return Objects.equals(sender, message.sender)
                && Objects.equals(receiver, message.receiver)
                && Objects.equals(text, message.text)
                && Objects.equals(timeSending, message.timeSending);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, receiver, text, timeSending);
    }
}
