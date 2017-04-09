package ua.sumdu.java.lab2.instant_messenger.listener;

import ua.sumdu.java.lab2.instant_messenger.config.parser.UserConfigParser;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public enum MultiThreadedServer implements Runnable {
    MULTI_THREADED_SERVER;

    public ExecutorService getService() {
        return service;
    }

    private ExecutorService service   = null;
    private ServerSocket serverSocket = null;
    private boolean isStopped    = false;

    @Override
    public void run() {
        openServerSocket();
        while(! isStopped()){
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if(isStopped()) {
                    //log debug;
                    return;
                }
                //log error Error accepting client connection
            }
            service.submit(new Worker(clientSocket));
        }
    }

    private void openServerSocket() {
        service = Executors.newCachedThreadPool();
        int serverPort = UserConfigParser.getCurrentUser().getPort();
        try {
            this.serverSocket = new ServerSocket(serverPort);
        } catch (IOException e) {
            //log
        }
    }

    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

}
