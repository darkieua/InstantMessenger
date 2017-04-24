package ua.sumdu.java.lab2.messenger.entities;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.java.lab2.messenger.api.UserMap;

public class UserMapImpl implements UserMap, Cloneable {

    private static final Logger LOG = LoggerFactory.getLogger(UserMapImpl.class);

    private Map<String, User> map;

    public ObservableList<User> getAllUsers() {
        allUsers = FXCollections.observableArrayList();
        for (User user : map.values()) {
            allUsers.add(user);
        }
        return allUsers;
    }

    private transient ObservableList<User> allUsers = FXCollections.observableArrayList();

    public UserMapImpl() {
        LOG.debug("Creating an object of class UserMapImpl");
        map = new TreeMap<>();
    }

    public Map<String, User> getMap() {
        return map;
    }

    public UserMap setMap(Map<String, User> map) {
        this.map = map;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        UserMapImpl userMap = (UserMapImpl) obj;
        return Objects.equals(map, userMap.map);
    }

    @Override
    public int hashCode() {
        return Objects.hash(map);
    }

    @Override
    public String toString() {
        return "UserMapImpl{" + "map=" + map + '}';
    }

    @Override
    public void addUser(ua.sumdu.java.lab2.messenger.entities.User user) {
        LOG.debug("Adding a user to UserMapImpl");
        String key = user.getEmail().split("@")[0];
        map.put(key, user);
        allUsers.add(user);
    }

    @Override
    public void removeUser(ua.sumdu.java.lab2.messenger.entities.User user) {
        LOG.debug("Delete a user from UserMapImpl");
        String key = user.getEmail().split("@")[0];
        map.remove(key);
        allUsers.remove(user);
    }
}
