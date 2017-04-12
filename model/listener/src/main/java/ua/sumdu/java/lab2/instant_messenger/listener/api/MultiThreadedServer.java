package ua.sumdu.java.lab2.instant_messenger.listener.api;

import java.util.concurrent.ExecutorService;

public interface MultiThreadedServer {
    ExecutorService getService();
    void openServerSocket();
    void stop();
}
