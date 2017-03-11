package ua.sumdu.java.lab2.instant_messenger.processing;

import ua.sumdu.java.lab2.instant_messenger.api.GroupMap;
import ua.sumdu.java.lab2.instant_messenger.api.UserMap;
import ua.sumdu.java.lab2.instant_messenger.entities.User;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class GroupMapImpl implements GroupMap {

    private Map<String, UserMap> map = new TreeMap<>();

    @Override
    public void addUser(String chatName, User user) {
        if (!map.containsKey(chatName)) {
            UserMap users = new UserMapImpl();
            map.put(chatName, users);
        }
        map.get(chatName).addUser(user);
    }

    @Override
    public void deleteUser(String chatName, User user) {
        if (map.containsKey(chatName)) {
            map.get(chatName).removeUser(user);
        }
    }

    public Map<String, UserMap> getMap() {
        return map;
    }

    public GroupMapImpl setMap(Map<String, UserMap> map) {
        this.map = map;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            System.out.println(this.getClass().toGenericString());
            return false;
        } else {
            Map current;
            if (GroupMap.class.equals(obj.getClass()) || GroupMapImpl.class.equals(obj.getClass())) {
                GroupMapImpl groupMap = (GroupMapImpl) obj;
                current = groupMap.getMap();
            } else {
                try {
                    current = (Map<String, UserMap>) obj;

                } catch (ClassCastException e) {
                    return false;
                }
            }
            if (map.size() != current.size()) {
                return false;
            }
            Set<Map.Entry<String, UserMap>> set = current.entrySet();
            for (Map.Entry value:set) {
                UserMapImpl users1 = (UserMapImpl) value.getValue();
                UserMapImpl users2 = (UserMapImpl) map.get(value.getKey());
                if (!users1.equals(users2)) {
                    return false;
                }
            }
            return true;
        }
    }
}
