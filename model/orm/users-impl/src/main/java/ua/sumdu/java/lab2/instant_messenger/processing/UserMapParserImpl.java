package ua.sumdu.java.lab2.instant_messenger.processing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ua.sumdu.java.lab2.instant_messenger.api.UserMap;
import ua.sumdu.java.lab2.instant_messenger.api.UserMapParser;
import ua.sumdu.java.lab2.instant_messenger.entities.UserMapImpl;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public final class UserMapParserImpl implements UserMapParser{
    private static UserMapParserImpl instance;

    private UserMapParserImpl() {
    }

    public static UserMapParserImpl getInstance() {
        synchronized (UserMapParserImpl.class) {
            if (instance == null) {
                instance = new UserMapParserImpl();
            }
            return instance;
        }
    }

    @Override
    public String userMapToJSonString(UserMap userMap) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        UserMapImpl newUsers = (UserMapImpl) userMap;
        return gson.toJson(newUsers);
    }

    @Override
    public UserMap jsonStringToUserMap(String jsonString) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        return gson.fromJson(jsonString, UserMapImpl.class);
    }

    @Override
    public void writeUserMapToFile(String jsonString) {
        try {
            Writer writer = new FileWriter("src/main/java/resources/groups.json");
            writer.write(jsonString);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
