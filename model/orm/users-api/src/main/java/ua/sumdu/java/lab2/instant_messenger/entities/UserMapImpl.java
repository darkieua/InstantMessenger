package ua.sumdu.java.lab2.instant_messenger.entities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.java.lab2.instant_messenger.api.UserMap;
import ua.sumdu.java.lab2.instant_messenger.common_entities.User;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class UserMapImpl implements UserMap, Cloneable {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserMapImpl.class);

    private Map<String, User> map;

    public UserMapImpl() {
        LOGGER.info("Creating an object of class UserMapImpl");
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
    public void addUser(User user) {
        LOGGER.info("Adding a user to UserMapImpl");
        String key = user.getEmail().split("@")[0];
        map.put(key, user);
    }

    @Override
    public void removeUser(User user) {
        LOGGER.info("Delete a user grom UserMapImpl");
        String key = user.getEmail().split("@")[0];
        map.remove(key);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            LOGGER.warn("Comparison UserMapImpl with null");
            return false;
        } else {
            Map<String, User> current;
            if (UserMap.class.equals(obj.getClass()) || UserMapImpl.class.equals(obj.getClass())) {
                LOGGER.info("Object types are the same");
                UserMapImpl userMap = (UserMapImpl) obj;
                current = userMap.getMap();
            } else {
                try {
                    LOGGER.info("Comparison UserMapImpl with map");
                    current = (Map<String, User>) obj;
                } catch (ClassCastException e) {
                    LOGGER.error("Comparison UserMapImpl: ClassCastException");
                    return false;
                }
            }
            if (map.size() != current.size()) {
                LOGGER.warn("Comparing objects of different lengths");
                return false;
            }
            Set<Map.Entry<String,User>> set = current.entrySet();
            for (Map.Entry value:set) {
                User user1 = (User) value.getValue();
                User user2 = map.get(value.getKey());
                if (!user1.equals(user2)) {
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
        for (Map.Entry<String, User> entry:map.entrySet()) {
            res = 13*res + entry.getKey().hashCode() + entry.getValue().hashCode();
        }
        return res;
    }

    public User clone() throws CloneNotSupportedException{
        LOGGER.info("Clone object");
        return (User) super.clone();
    }
}
