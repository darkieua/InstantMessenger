package ua.sumdu.java.lab2.messenger.listener.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.java.lab2.messenger.entities.User;
import ua.sumdu.java.lab2.messenger.handler.processing.RequestParsingImpl;
import ua.sumdu.java.lab2.messenger.handler.processing.ResponseGeneratingImpl;
import ua.sumdu.java.lab2.messenger.listener.api.MultiThreadedServer;

public class MultiThreadedServerImpl extends Thread implements MultiThreadedServer {

    private static final Logger LOG = LoggerFactory.getLogger(MultiThreadedServerImpl.class);

    public MultiThreadedServerImpl() {
        this.service = Executors.newCachedThreadPool();
    }

    public ExecutorService getService() {
        return service;
    }

    public void setService(ExecutorService service) {
        this.service = service;
    }

    private ExecutorService service;
    private ServerSocket serverSocket;
    private boolean work;

    @Override
    public void run() {
        openServerSocket();
        while (work) {
            try {
                Socket clientSocket = serverSocket.accept();
                service.submit(() -> { processingRequest(clientSocket);
                });
            } catch (IOException e) {
                LOG.error("Error accepting client connection");
            }
        }
    }

    private void processingRequest(Socket clientSocket) {
        try {
            OutputStream output = clientSocket.getOutputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(
                    clientSocket.getInputStream());
            BufferedReader input = new BufferedReader(inputStreamReader);
            StringBuilder request = new StringBuilder();
            LineIterator iterator = IOUtils.lineIterator(input);
            while (iterator.hasNext()) {
                request.append(iterator.nextLine());
            }
            RequestParsingImpl requestParsing = new RequestParsingImpl();
            String result = requestParsing.requestParsing(request.toString());
            ResponseGeneratingImpl responseGenerating = new ResponseGeneratingImpl();
            output.write(responseGenerating.responseGenerate(result).getBytes());
            output.flush();
            output.close();
            iterator.close();
            input.close();
            clientSocket.close();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }


    /**
    * Starting the server.
    */

    public void openServerSocket() {
        this.work = true;
        this.serverSocket = startServet();
    }

    public ServerSocket startServet() {
        int serverPort = User.getCurrentUser().getPort();
        try {
             return new ServerSocket(serverPort);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }



    /**
    * Shutdown the server.
    */

    public void stopServer() {
        this.work = false;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            LOG.error("Error closing server", e);
        }
    }

}
