package ua.sumdu.java.lab2.messenger.entities;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MessageCounter {

    private final Map<String, Integer> map;

    public MessageCounter() {
        map = new TreeMap<>();
    }

    public Map<String, Integer> getMap() {
        return map;
    }

    public ObservableList<String> getUserList() {
        return FXCollections.observableArrayList(map.keySet());
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
