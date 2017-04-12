package ua.sumdu.java.lab2.instant_messenger.listener.impl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import ua.sumdu.java.lab2.instant_messenger.handler.processing.ResponseParsingImpl;
import ua.sumdu.java.lab2.instant_messenger.listener.api.Client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class ClientImpl extends Thread implements Client {
    private Socket socket;
    private String request;
    private String response;

    public ClientImpl(InetAddress adr, int port, String request) {
        socketInit(adr, port);
        this.request = request;
    }

    @Override
    public void run() {
        this.response = interactionWithServer();
        ResponseParsingImpl responseParsing = new ResponseParsingImpl();
        responseParsing.parse(response);

    }


    @Override
    public void socketInit(InetAddress adr, int port) {
        try (Socket socket = new Socket(adr, port)) {
            this.socket = socket;
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    @Override
    public String interactionWithServer() {
        try {
            OutputStream out = socket.getOutputStream();
            out.write(request.getBytes());
            out.flush();
            socket.shutdownOutput();
            this.sleep(1000);
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            BufferedReader in = new BufferedReader(inputStreamReader);
            StringBuilder response = new StringBuilder();
            LineIterator it = IOUtils.lineIterator(in);
            while (it.hasNext()) {
                response.append(it.nextLine());
            }
            out.close();
            in.close();
            socket.close();
            return response.toString();
        } catch (InterruptedException | IOException e) {
            //e.printStackTrace();
            return null;
        }
    }
}
