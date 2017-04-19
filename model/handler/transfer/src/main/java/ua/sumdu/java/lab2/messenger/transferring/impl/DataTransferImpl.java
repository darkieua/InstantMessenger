package ua.sumdu.java.lab2.messenger.transferring.impl;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.time.LocalDateTime;
import ua.sumdu.java.lab2.messenger.entities.Message;
import ua.sumdu.java.lab2.messenger.entities.MessageMapImpl;
import ua.sumdu.java.lab2.messenger.entities.SentFiles;
import ua.sumdu.java.lab2.messenger.entities.User;
import ua.sumdu.java.lab2.messenger.parsers.XmlParser;
import ua.sumdu.java.lab2.messenger.transferring.api.DataTransfer;

public class DataTransferImpl implements DataTransfer {

  @Override
  public String dataRequest(SentFiles files) {
    return files.toJSonString();
  }

  @Override
  public String requestParsing(String context) {
    String[] words = context.split("==");
    SentFiles newFiles = userInteraction(SentFiles.fromJson(words[1]));
    if (newFiles.size() == 0) {
      return "";
    } else {
      return newFiles.toJSonString();
    }
  }

  @Override
  public String dataAcquisition(String response) {
    StringBuilder result = new StringBuilder();
    String ipAddress = String.valueOf(User.getCurrentUser().getIpAddress());
    result.append(ipAddress).append(':');
    int port = getFreePort();
    SendingAndReceivingFilesImpl sendingAndReceivingFiles = getSendingFilesElements();
    sendingAndReceivingFiles.listenPort(port, SentFiles.fromJson(response));
    result.append(port).append("==").append(response);
    return result.toString();
  }

  @Override
  public String parsingDataAcquisitionResponse(String context) {
    String[] words = context.split("==");
    String host = words[0];
    SentFiles files = SentFiles.fromJson(context.substring(words[0].length() + 2));
    SendingAndReceivingFilesImpl sendingAndReceivingFiles = getSendingFilesElements();
    sendingAndReceivingFiles.sendingFiles(host, files);
    return "";
  }

  @Override
  public String parsingDataSendingRejectedResponse(String context) {
    File system = User.getSystemMessageFile();
    MessageMapImpl messages = (MessageMapImpl) XmlParser.INSTANCE.read(system);
    Message newMessage = new Message("system", User.getCurrentUser().getUsername(),
        "User " + context + " declined to receive files", LocalDateTime.now());
    messages.addMessage(newMessage);
    XmlParser.INSTANCE.write(messages, system);
    return "";
  }

  public int getFreePort() {
    try {
      ServerSocket serverSocket = new ServerSocket(0);
      int port = serverSocket.getLocalPort();
      serverSocket.close();
      return port;
    } catch (IOException var2) {
      //throw new IllegalStateException(var2);
      return -1;
    }
  }

  public SendingAndReceivingFilesImpl getSendingFilesElements() {
    return new SendingAndReceivingFilesImpl();
  }

  public SentFiles userInteraction(SentFiles files) {
    return files; //controller
  }
}
