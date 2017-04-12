package ua.sumdu.java.lab2.instant_messenger.listener.impl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.java.lab2.instant_messenger.handler.processing.ResponseParsingImpl;
import ua.sumdu.java.lab2.instant_messenger.listener.api.Client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class ClientImpl extends Thread implements Client {

    private static final Logger LOG = LoggerFactory.getLogger(ClientImpl.class);

    private Socket socket;
    private final String request;

    public ClientImpl(InetAddress adr, int port, String request) {
        socketInit(adr, port);
        this.request = request;
    }

    @Override
    public void run() {
        String response = interactionWithServer();
        ResponseParsingImpl responseParsing = new ResponseParsingImpl();
        responseParsing.parse(response);

    }


    @Override
    public void socketInit(InetAddress adr, int port) {
        try (Socket socket = new Socket(adr, port)) {
            this.socket = socket;
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public String interactionWithServer() {
        try {
            OutputStream out = socket.getOutputStream();
            out.write(request.getBytes());
            out.flush();
            socket.shutdownOutput();
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            BufferedReader in = new BufferedReader(inputStreamReader);
            StringBuilder response = new StringBuilder();
            LineIterator iterator = IOUtils.lineIterator(in);
            while (iterator.hasNext()) {
                response.append(iterator.nextLine());
            }
            out.close();
            in.close();
            socket.close();
            return response.toString();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }
}
