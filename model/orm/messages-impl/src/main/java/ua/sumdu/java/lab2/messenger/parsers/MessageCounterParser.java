package ua.sumdu.java.lab2.messenger.parsers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import ua.sumdu.java.lab2.messenger.entities.MessageCounter;
import ua.sumdu.java.lab2.messenger.entities.User;

import java.io.File;
import java.io.IOException;

public enum MessageCounterParser {
    PARSER;
    public MessageCounter getMessageCounter() {
        try {
            File file = User.getNewMessageFile();
            if (!file.exists()) {
                file.createNewFile();
                return new MessageCounter();
            }
            String jsonString = FileUtils.readFileToString(file, "UTF-8");
            return toMessageCounter(jsonString);
        } catch (IOException e) {
            return new MessageCounter();
        }
    }

    public void write(MessageCounter messageCounter) {
        try {
            File file = User.getNewMessageFile();
            if (!file.exists()) {
                file.createNewFile();
            }
            FileUtils.writeStringToFile(file, messageCounterToJSonString(messageCounter), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String messageCounterToJSonString(MessageCounter messageCounter) {
        return new GsonBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(messageCounter);
    }

    public MessageCounter toMessageCounter(String jsonString) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        return gson.fromJson(jsonString, MessageCounter.class);
    }
}
