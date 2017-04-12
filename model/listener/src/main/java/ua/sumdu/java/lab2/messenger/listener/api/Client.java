package ua.sumdu.java.lab2.messenger.listener.api;

import java.net.InetAddress;

public interface Client {
  void socketInit(InetAddress adr, int port);

  String interactionWithServer();
}