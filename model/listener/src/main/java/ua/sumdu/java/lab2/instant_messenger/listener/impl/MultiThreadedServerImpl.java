package ua.sumdu.java.lab2.instant_messenger.listener.impl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import ua.sumdu.java.lab2.instant_messenger.handler.processing.RequestParsingImpl;
import ua.sumdu.java.lab2.instant_messenger.handler.processing.ResponseGeneratingImpl;
import ua.sumdu.java.lab2.instant_messenger.listener.api.MultiThreadedServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadedServerImpl extends Thread implements MultiThreadedServer  {

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
                        LineIterator it = IOUtils.lineIterator(input);
                        while (it.hasNext()) {
                            request.append(it.nextLine());
                        }
                        RequestParsingImpl requestParsing = new RequestParsingImpl();
                        String result = requestParsing.parse(request.toString());
                        ResponseGeneratingImpl responseGenerating = new ResponseGeneratingImpl();
                        output.write(responseGenerating.generate(result).getBytes());
                        output.flush();
                        output.close();
                        it.close();
                        input.close();
                        clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                //log error Error accepting client connection
            }
        }
    }

    public void openServerSocket() {
        service = Executors.newCachedThreadPool();
        //int serverPort = UserConfigParser.getCurrentUser().getPort();
        try {
            this.serverSocket = new ServerSocket(10510);
            this.work = true;
        } catch (IOException e) {
            //log
        }
    }

    public synchronized void stopServer(){
        this.work = false;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            //throw new RuntimeException("Error closing server", e);
        }
    }

}
