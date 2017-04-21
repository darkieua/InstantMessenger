package ua.sumdu.java.lab2.messenger.transferring.impl;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.time.LocalDateTime;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.java.lab2.messenger.entities.Message;
import ua.sumdu.java.lab2.messenger.entities.MessageMapImpl;
import ua.sumdu.java.lab2.messenger.entities.SentFiles;
import ua.sumdu.java.lab2.messenger.entities.User;
import ua.sumdu.java.lab2.messenger.parsers.XmlParser;
import ua.sumdu.java.lab2.messenger.transferring.api.DataTransfer;

public class DataTransferImpl implements DataTransfer {
  private static final Logger LOG = LoggerFactory.getLogger(DataTransferImpl.class);

  @Override
  public String dataRequest(SentFiles files) {
    return files.toJSonString();
  }

  @Override
  public String requestParsing(String context) {
    String[] words = context.split("==");
    SentFiles files =  SentFiles.fromJson(words[1]);
    files.updateObs();
    SentFiles newFiles = userInteraction(words[0],files);
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
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
      return -1;
    }
  }

  public SendingAndReceivingFilesImpl getSendingFilesElements() {
    return new SendingAndReceivingFilesImpl();
  }

  public SentFiles userInteraction(String name, SentFiles files) {
    final SentFiles[] sentFiles = new SentFiles[1];
    boolean[] work = {true};
    int time = 0;
    Platform.runLater(() -> {
      Stage stage = new Stage();
      FXMLLoader receivingFilesFxmlLoader = new FXMLLoader();
      receivingFilesFxmlLoader.setLocation(getClass().getResource("../ReceivingFiles.fxml"));
      Parent root = null;
      try {
        root = receivingFilesFxmlLoader.load();
      } catch (IOException e) {
        LOG.error(e.getMessage(), e);
      }
      ReceivingFilesController receivingFilesController = receivingFilesFxmlLoader.getController();
      receivingFilesController.setSentFiles(files);
      receivingFilesController.setName(name);
      stage.setTitle("Receiving Files");
      stage.setScene(new Scene(root, 400, 350));
      stage.setResizable(false);
      stage.showAndWait();
      sentFiles[0] = receivingFilesController.getNewFileList();
      work[0] = false;
    });
    while(work[0] && time < 1791) {
      time ++;
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        LOG.error(e.getMessage());
      }
    }
    return sentFiles[0];
  }
}
