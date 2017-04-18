package ua.sumdu.java.lab2.messenger.listener.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.java.lab2.messenger.handler.processing.ResponseGeneratingImpl;
import ua.sumdu.java.lab2.messenger.handler.processing.ResponseParsingImpl;
import ua.sumdu.java.lab2.messenger.listener.api.Client;

public class ClientImpl extends Thread implements Client {

  private static final Logger LOG = LoggerFactory.getLogger(ClientImpl.class);

  private Socket socket;
  private final String request;
  private final InetAddress adr;
  private final boolean isConnect;

  public ClientImpl(InetAddress adr, int port, String request) {
    isConnect = socketInit(adr, port);
    this.request = request;
    this.adr = adr;
  }

  @Override
  public void run() {
    if (isConnect) {
      String response = interactionWithServer();
      ResponseParsingImpl responseParsing = new ResponseParsingImpl();
      String context = responseParsing.responseParsing(response);
      if (!"".equals(context)) {
        String[] words = context.split("=");
        Distribution.sendOutNewGroupList(words[0], words[1]);
      }
    } else {
      ResponseGeneratingImpl responseGenerating = new ResponseGeneratingImpl();
      ResponseParsingImpl responseParsing = new ResponseParsingImpl();
      responseParsing.responseParsing(responseGenerating.userIsOffline(adr.getHostAddress()));
    }
  }

  @Override
  public boolean socketInit(InetAddress adr, int port) {
    this.socket = openSocket(adr, port);
    return Objects.nonNull(socket);
  }

  public Socket openSocket(InetAddress adr, int port) {
    try {
      return new Socket(adr, port);
    } catch (IOException e) {
      return null;
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
      return response.toString();
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
    }
    return "";
  }
}
