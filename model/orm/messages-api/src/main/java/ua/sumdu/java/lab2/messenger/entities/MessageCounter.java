package ua.sumdu.java.lab2.messenger.entities;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MessageCounter {

    private Map<String, Integer> map = new TreeMap<>();

    private transient ObservableList<String> userList;


    public Map<String, Integer> getMap() {
        return map;
    }

    public ObservableList<String> getUserList() {
        userList = FXCollections.observableArrayList(map.keySet());
        return userList;
    }

    public void add(String username, int countMessages) {
        if (Objects.isNull(map.get(username))) {
            map.put(username, countMessages);
        } else {
            int count = map.get(username);
            map.replace(username, count, count+countMessages);
        }
    }

    public void remove(String username) {
        map.remove(username);
    }
}
