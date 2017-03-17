package ua.sumdu.java.lab2.instant_messenger.entities;

import java.time.LocalDateTime;
import java.util.List;

public class Message {
    private User sender;
    private User receiver;
    private String test;
    private LocalDateTime timeSending;
    private List<TransferredFile> fileList;

    public Message(User sender, User receiver, String test, LocalDateTime timeSending, List<TransferredFile> fileList) {
        this.sender = sender;
        this.receiver = receiver;
        this.test = test;
        this.timeSending = timeSending;
        this.fileList = fileList;
    }

    public Message(User sender, User receiver, String test, LocalDateTime timeSending) {

        this.sender = sender;
        this.receiver = receiver;
        this.test = test;
        this.timeSending = timeSending;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public LocalDateTime getTimeSending() {
        return timeSending;
    }

    public void setTimeSending(LocalDateTime timeSending) {
        this.timeSending = timeSending;
    }

    public List<TransferredFile> getFileList() {
        return fileList;
    }

    public void setFileList(List<TransferredFile> fileList) {
        this.fileList = fileList;
    }
}
