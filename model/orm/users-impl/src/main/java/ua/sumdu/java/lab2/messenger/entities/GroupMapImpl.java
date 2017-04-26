package ua.sumdu.java.lab2.messenger.entities;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.java.lab2.messenger.api.GroupMap;
import ua.sumdu.java.lab2.messenger.api.UserMap;

public class GroupMapImpl implements GroupMap, Cloneable {

    private static final Logger LOG = LoggerFactory.getLogger(GroupMapImpl.class);

    private Map<String, UserMapImpl> map;

    public GroupMapImpl() {
        map = new TreeMap<>();
        LOG.debug("Creating an object of class GroupMapImpl");
    }

    @Override
    public void addUser(String chatName, User user) {
        LOG.debug("Adding a user to GroupMapImpl");
        if (!map.containsKey(chatName)) {
            UserMapImpl users = new UserMapImpl();
            map.put(chatName, users);
        }
        UserMap userMap = map.get(chatName);
        if (Objects.isNull(userMap)) {
            map.put(chatName, new UserMapImpl());
        }
        map.get(chatName).addUser(user);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        GroupMapImpl groupMap = (GroupMapImpl) obj;
        return Objects.equals(map, groupMap.map);
    }

    @Override
    public int hashCode() {
        return Objects.hash(map);
    }

    @Override
    public void deleteUser(String chatName, User user) {
        LOG.debug("Delete a user grom GroupMapImpl");

        if (map.containsKey(chatName)) {
            map.get(chatName).removeUser(user);
        }
    }

    public Map<String, UserMap> getMap() {
        Map<String, UserMap> newMap = new TreeMap<>();
        for (Map.Entry<String, UserMapImpl> pair : map.entrySet()) {
            newMap.put(pair.getKey(), pair.getValue());
        }
        return newMap;
    }

    public GroupMap setMap(Map<String, UserMap> newMap) {
        this.map = new TreeMap<>();
        for (Map.Entry<String, UserMap> pair : newMap.entrySet()) {
            this.map.put(pair.getKey(), (UserMapImpl) pair.getValue());
        }
        return this;
    }

    @Override
    public String toString() {
        return "GroupMapImpl{" + "map=" + map + '}';
    }
}
