package ua.sumdu.java.lab2.instant_messenger.listener;

import ua.sumdu.java.lab2.instant_messenger.handler.api.ResponseParsing;
import ua.sumdu.java.lab2.instant_messenger.handler.processing.ResponseGeneratingImpl;
import ua.sumdu.java.lab2.instant_messenger.handler.processing.ResponseParsingImpl;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client implements Runnable{
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String request;

    public Client(InetAddress adr, int port, String request) {
        try {
            socket = new Socket(adr, port);
        }
        catch (IOException e) {
            //log Socket failed
        }
        try {
            in = new BufferedReader(new InputStreamReader(socket
                    .getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    socket.getOutputStream())), true);
        }
        catch (IOException e) {
            try {
                socket.close();
            }
            catch (IOException e2) {
                // log Socket not closed
            }
        }
    }

    @Override
    public void run() {
        out.write(request);
        out.close();
        String thisLine;
        StringBuilder response = new StringBuilder();
        try {
            while ((thisLine = in.readLine()) != null) {
                response.append(thisLine);
            }
        } catch (IOException e) {
            //log e.printStackTrace();
        }
        ResponseParsingImpl responseParsing = new ResponseParsingImpl();
        responseParsing.parse(response.toString());
    }
}
