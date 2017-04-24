package ua.sumdu.java.lab2.messenger.transferring.api;

import ua.sumdu.java.lab2.messenger.entities.SentFiles;

public interface SendingAndReceivingFiles {

    void listenPort(int port, SentFiles files);

    void sendingFiles(String host, SentFiles files);
}
