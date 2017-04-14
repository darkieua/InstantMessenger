package ua.sumdu.java.lab2.messenger.api;

import java.io.IOException;
import ua.sumdu.java.lab2.messenger.entities.Message;

public interface MessageMap {

  void addMessage(Message message) throws IOException;

  void deleteMessage(Message message) throws IOException;
}
