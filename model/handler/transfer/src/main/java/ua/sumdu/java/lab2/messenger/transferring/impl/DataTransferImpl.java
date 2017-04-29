package ua.sumdu.java.lab2.messenger.transferring.impl;

import java.io.IOException;
import java.net.ServerSocket;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.java.lab2.messenger.entities.SentFiles;
import ua.sumdu.java.lab2.messenger.entities.User;
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
        SentFiles files =    SentFiles.fromJson(words[1]);
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
        String ipAddress = String.valueOf(User.getCurrentUser().getIpAddress()).substring(1);
        result.append(ipAddress).append(':');
        int port = getFreePort();
        SendingAndReceivingFilesImpl sendingAndReceivingFiles = getSendingFilesElements();
        new Thread(() -> sendingAndReceivingFiles.listenPort(port, SentFiles.fromJson(response))).start();
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
        Platform.runLater(() -> {
            Notifications notification = Notifications.create()
                    .title("Information")
                    .darkStyle()
                    .graphic(null)
                    .text("User " + context + " declined to receive files")
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.BOTTOM_RIGHT);
            notification.showConfirm();
        });
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
            receivingFilesFxmlLoader.setLocation(getClass().getResource("/ua/sumdu/java/lab2/messenger/transferring/ReceivingFiles.fxml"));
            Parent root = null;
            try {
                root = receivingFilesFxmlLoader.load();
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
            ReceivingFilesController receivingFilesController = receivingFilesFxmlLoader.getController();
            receivingFilesController.setSentFiles(files);
            receivingFilesController.setName(name);
            receivingFilesController.initAfterAddParametrs();
            stage.setTitle("Receiving Files");
            stage.setScene(new Scene(root, 500, 400));
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
