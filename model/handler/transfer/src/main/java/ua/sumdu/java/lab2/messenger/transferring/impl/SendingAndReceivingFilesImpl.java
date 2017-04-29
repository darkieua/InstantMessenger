package ua.sumdu.java.lab2.messenger.transferring.impl;

import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.java.lab2.messenger.entities.SentFiles;
import ua.sumdu.java.lab2.messenger.entities.User;
import ua.sumdu.java.lab2.messenger.transferring.api.SendingAndReceivingFiles;

public class SendingAndReceivingFilesImpl implements SendingAndReceivingFiles {
    private static final Logger LOG = LoggerFactory.getLogger(SendingAndReceivingFilesImpl.class);

    @Override
    public void listenPort(int port, SentFiles files) {
        File currentFile = null;
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            Socket socket = serverSocket.accept();
            InputStream socketIn = socket.getInputStream();
            for (SentFiles.FileCharacteristics file : files.getList()) {
                long size = file.getSize();
                String name = file.getName();
                File newFile = new File(User.getDirectoryForDownloadFiles() + File.separator + name);
                currentFile = newFile;
                newFile.createNewFile();
                FileOutputStream fos = new FileOutputStream(newFile);
                byte[] buffer = new byte[1024];
                long temp = size;
                while (temp > 0) {
                    if (temp / 1024 == 0) {
                        int len = Math.toIntExact(temp % 1024);
                        byte[] last = new byte[len];
                        int read = socketIn.read(last);
                        fos.write(last, 0, read);
                        temp = 0;
                    } else {
                        int read = socketIn.read(buffer);
                        fos.write(buffer, 0, read);
                        temp -= 1024;
                    }
                    fos.flush();
                }
                fos.close();
            }
             socket.close();
            Platform.runLater(() -> {
                Notifications notification = Notifications.create()
                        .title("Download complete")
                        .darkStyle()
                        .graphic(null)
                        .text("Files successfully uploaded")
                        .hideAfter(Duration.seconds(5))
                        .onAction((event) -> {
                            Desktop desktop = null;
                            if (Desktop.isDesktopSupported()) {
                                desktop = Desktop.getDesktop();
                            }
                            try {
                                desktop.open(new File(User.getDirectoryForDownloadFiles()));
                            } catch (IOException e1) {
                                LOG.error(e1.getMessage(), e1);
                            }
                        })
                        .position(Pos.BOTTOM_RIGHT);
                notification.showConfirm();
            });
        } catch (IOException e) {
            Notifications notification = Notifications.create()
                    .title("Error")
                    .darkStyle()
                    .graphic(null)
                    .text("Disconnection. Files were not uploaded")
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.BOTTOM_RIGHT);
            notification.showError();
            if (Objects.nonNull(currentFile)) {
                boolean result = currentFile.delete();
                if (!result) {
                    LOG.error("File " + currentFile.getName() + " was not deleted.");
                }
            }
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public void sendingFiles(String host, SentFiles files) {
        try {
            String ipAddress = host.split(":")[0];
            int port = Integer.parseInt(host.split(":")[1]);
            Socket socket = new Socket(ipAddress, port);
            OutputStream socketOut = socket.getOutputStream();
            for (SentFiles.FileCharacteristics file : files.getList()) {
                long size = file.getSize();
                FileInputStream fis = new FileInputStream(file.getPath());
                int bufferSize = 2*1024;
                byte[] buffer = new byte[bufferSize];
                long temp = size;
                while (temp > 0) {
                    if (temp / bufferSize == 0) {
                        int len = Math.toIntExact(temp % bufferSize);
                        byte[] last = new byte[len];
                        int read = fis.read(last);
                        socketOut.write(last, 0, read);
                        temp = 0;
                    } else {
                        int read = fis.read(buffer);
                        socketOut.write(buffer, 0, read);
                        temp -= bufferSize;
                    }
                    socketOut.flush();
                }
                fis.close();
            }
            socketOut.flush();
            socketOut.close();
            socket.close();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }

    }
}
