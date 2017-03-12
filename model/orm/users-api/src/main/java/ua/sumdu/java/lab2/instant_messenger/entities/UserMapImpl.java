package ua.sumdu.java.lab2.instant_messenger.entities;

import ua.sumdu.java.lab2.instant_messenger.api.UserMap;
import ua.sumdu.java.lab2.instant_messenger.common_entities.User;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class UserMapImpl implements UserMap {

    private Map<String, User> map = new TreeMap<>();

    public UserMapImpl() {
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
        String key = user.getEmail().split("@")[0];
        map.put(key, user);
    }

    @Override
    public void removeUser(User user) {
        String key = user.getEmail().split("@")[0];
        map.remove(key);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            System.out.println(this.getClass().toGenericString());
            return false;
        } else {
            Map<String, User> current;
            if (UserMap.class.equals(obj.getClass()) || UserMapImpl.class.equals(obj.getClass())) {
                UserMapImpl userMap = (UserMapImpl) obj;
                current = userMap.getMap();
            } else {
                try {
                    current = (Map<String, User>) obj;
                } catch (Exception e) {
                    return false;
                }
            }
            if (map.size() != current.size()) {
                return false;
            }
            Set<Map.Entry<String,User>> set = current.entrySet();
            for (Map.Entry value:set) {
                User user1 = (User) value.getValue();
                User user2 = map.get(value.getKey());
                if (!user1.equals(user2)) {
                    return false;
                }
            }
            return true;
        }
    }

}
