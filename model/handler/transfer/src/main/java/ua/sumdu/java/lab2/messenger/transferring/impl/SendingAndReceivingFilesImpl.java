package ua.sumdu.java.lab2.messenger.transferring.impl;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import ua.sumdu.java.lab2.messenger.entities.SentFiles;
import ua.sumdu.java.lab2.messenger.entities.User;
import ua.sumdu.java.lab2.messenger.transferring.api.SendingAndReceivingFiles;

public class SendingAndReceivingFilesImpl implements SendingAndReceivingFiles {
  @Override
  public void listenPort(int port, SentFiles files) {
    try {
      ServerSocket serverSocket = new ServerSocket(port);
      Socket socket = serverSocket.accept();
      InputStream socketIn = socket.getInputStream();
      for (SentFiles.FileCharacteristics file : files.getList()) {
        long size = file.getSize();
        String name = file.getName();
        File newFile = new File(User.getDirectoryForDownloadFiles() + File.separator + name);
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
    } catch (IOException e) {
      throw new IllegalStateException(e);
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
        String name = file.getName();
        FileInputStream fis = new FileInputStream(file.getPath() + File.separator + name);
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
      e.printStackTrace();
    }

  }
}
