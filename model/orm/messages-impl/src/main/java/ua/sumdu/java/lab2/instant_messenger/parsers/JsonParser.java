package ua.sumdu.java.lab2.instant_messenger.parsers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.java.lab2.instant_messenger.api.MessageMap;
import ua.sumdu.java.lab2.instant_messenger.api.MessageMapParser;
import ua.sumdu.java.lab2.instant_messenger.entities.MessageMapImpl;

import java.io.*;

public final class JsonParser implements MessageMapParser{
    private static final Logger LOG = LoggerFactory.getLogger(JsonParser.class);

    private static JsonParser instance;

    private JsonParser() {
    }

    public static JsonParser getInstance() {
        synchronized (JsonParser.class) {
            LOG.debug("Create a new UserCreator");
            if (instance == null) {
                instance = new JsonParser();
            }
            return instance;
        }
    }

    @Override
    public boolean write(MessageMap map, File file) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setLenient().create();
        try {
            FileUtils.writeStringToFile(file, gson.toJson(map), "UTF-8");
            return true;
        } catch (IOException e) {
            LOG.warn(e.getMessage());
            return false;
        }
    }

    @Override
    public MessageMap read(File file) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setLenient().create();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String jsonString = reader.readLine();
            System.out.println(jsonString);
            MessageMapImpl messageMap = gson.fromJson(jsonString, MessageMapImpl.class);
            return messageMap;
        } catch (IOException e) {
            LOG.warn(e.getMessage());
            return null;
        }
    }
}
