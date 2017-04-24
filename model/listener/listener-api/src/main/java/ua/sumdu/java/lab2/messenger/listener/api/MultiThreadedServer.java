package ua.sumdu.java.lab2.messenger.listener.api;

import java.util.concurrent.ExecutorService;

public interface MultiThreadedServer {

    ExecutorService getService();

    void openServerSocket();

    void stopServer();
}
