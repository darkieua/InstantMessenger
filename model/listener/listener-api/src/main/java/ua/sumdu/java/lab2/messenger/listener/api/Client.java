package ua.sumdu.java.lab2.messenger.listener.api;

import java.net.InetAddress;

public interface Client {

    boolean socketInit(InetAddress adr, int port);

    String interactionWithServer();
}
