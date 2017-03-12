package ua.sumdu.java.lab2.instant_messenger.entities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.java.lab2.instant_messenger.api.GroupMap;
import ua.sumdu.java.lab2.instant_messenger.common_entities.User;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class GroupMapImpl implements GroupMap, Cloneable {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupMapImpl.class);

    private Map<String, UserMapImpl> map;

    public GroupMapImpl() {
        map = new TreeMap<>();
        LOGGER.info("Creating an object of class GroupMapImpl");
    }

    @Override
    public void addUser(String chatName, User user) {
        LOGGER.info("Adding a user to GroupMapImpl");
        if (!map.containsKey(chatName)) {
            UserMapImpl users = new UserMapImpl();
            map.put(chatName, users);
        }
        map.get(chatName).addUser(user);
    }

    @Override
    public void deleteUser(String chatName, User user) {
        LOGGER.info("Delete a user grom GroupMapImpl");
        if (map.containsKey(chatName)) {
            map.get(chatName).removeUser(user);
        }
    }

    public Map<String, UserMapImpl> getMap() {
        return map;
    }

    public GroupMapImpl setMap(Map<String, UserMapImpl> map) {
        this.map = map;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            LOGGER.warn("Comparison GroupMapImpl with null");
            return false;
        } else {
            Map current;
            if (GroupMap.class.equals(obj.getClass()) || GroupMapImpl.class.equals(obj.getClass())) {
                LOGGER.info("Object types are the same");
                GroupMapImpl groupMap = (GroupMapImpl) obj;
                current = groupMap.getMap();
            } else {
                try {
                    LOGGER.info("Comparison GroupMapImpl with map");
                    current = (Map<String, UserMapImpl>) obj;

                } catch (ClassCastException e) {
                    LOGGER.error("Comparison GroupMapImpl: ClassCastException");
                    return false;
                }
            }
            if (map.size() != current.size()) {
                LOGGER.warn("Comparing objects of different lengths");
                return false;
            }
            Set<Map.Entry<String, UserMapImpl>> set = current.entrySet();
            for (Map.Entry value:set) {
                UserMapImpl users1 = (UserMapImpl) value.getValue();
                UserMapImpl users2 = map.get(value.getKey());
                if (!users1.equals(users2)) {
                    LOGGER.warn("Comparing objects are different");
                    return false;
                }
            }
            LOGGER.info("Comparing objects are identical");
            return true;
        }
    }

    @Override
    public int hashCode() {
        LOGGER.info("Using hashCode function");
        int res = 0;
        for (Map.Entry<String, UserMapImpl> entry:map.entrySet()) {
            res = 13*res + entry.getKey().hashCode() + entry.getValue().hashCode();
        }
        return res;
    }

    public User clone() throws CloneNotSupportedException{
        LOGGER.info("Clone object");
        return (User) super.clone();
    }
}
