package ua.sumdu.java.lab2.instant_messenger.listener.impl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.java.lab2.instant_messenger.handler.processing.RequestParsingImpl;
import ua.sumdu.java.lab2.instant_messenger.handler.processing.ResponseGeneratingImpl;
import ua.sumdu.java.lab2.instant_messenger.listener.api.MultiThreadedServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ua.sumdu.java.lab2.instant_messenger.entities.User.CURRENT_USER;

public class MultiThreadedServerImpl extends Thread implements MultiThreadedServer  {

    private static final Logger LOG = LoggerFactory.getLogger(ClientImpl.class);

    public ExecutorService getService() {
        return service;
    }

    private ExecutorService service;
    private ServerSocket serverSocket;
    private boolean work;

    @Override
    public void run() {
        openServerSocket();
        while(work){
            try {
                Socket clientSocket = serverSocket.accept();
                service.submit(() ->{
                    try {
                        OutputStream output = clientSocket.getOutputStream();
                        InputStreamReader inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
                        BufferedReader input = new BufferedReader(inputStreamReader);
                        StringBuilder request = new StringBuilder();
                        LineIterator iterator = IOUtils.lineIterator(input);
                        while (iterator.hasNext()) {
                            request.append(iterator.nextLine());
                        }
                        RequestParsingImpl requestParsing = new RequestParsingImpl();
                        String result = requestParsing.requestParser(request.toString());
                        ResponseGeneratingImpl responseGenerating = new ResponseGeneratingImpl();
                        output.write(responseGenerating.generate(result).getBytes());
                        output.flush();
                        output.close();
                        iterator.close();
                        input.close();
                        clientSocket.close();
                    } catch (IOException e) {
                        LOG.error(e.getMessage(), e);
                    }
                });
            } catch (IOException e) {
                LOG.error("Error accepting client connection", e);
            }
        }
    }

    public void openServerSocket() {
        service = Executors.newCachedThreadPool();
        int serverPort = CURRENT_USER.getPort();
        try {
            this.serverSocket = new ServerSocket(serverPort);
            this.work = true;
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public void stopServer(){
        this.work = false;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            LOG.error("Error closing server", e);
        }
    }

}
