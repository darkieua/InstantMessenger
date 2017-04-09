package ua.sumdu.java.lab2.instant_messenger.listener;

import ua.sumdu.java.lab2.instant_messenger.handler.processing.RequestParsingImpl;
import ua.sumdu.java.lab2.instant_messenger.handler.processing.ResponseGeneratingImpl;

import java.io.*;
import java.net.Socket;

public class Worker implements Runnable {

    protected Socket clientSocket = null;

    public Worker(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            PrintWriter output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    clientSocket.getOutputStream())), true);
            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            StringBuilder request = new StringBuilder();
            String thisLine;
            while ((thisLine = input.readLine()) != null) {
                request.append(thisLine);
            }
            RequestParsingImpl requestParsing = new RequestParsingImpl();
            String result = requestParsing.parse(request.toString());
            ResponseGeneratingImpl responseGenerating = new ResponseGeneratingImpl();
            output.write(responseGenerating.generate(result));
            output.close();
            input.close();
        } catch (IOException e) {
            try {
                clientSocket.close();
            } catch (IOException e1) {
                //log
            }
            //log
        }
    }
}
