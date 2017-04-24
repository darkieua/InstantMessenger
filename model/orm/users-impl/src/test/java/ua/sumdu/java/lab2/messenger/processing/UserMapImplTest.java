package ua.sumdu.java.lab2.messenger.processing;

import static org.junit.Assert.assertTrue;
import static ua.sumdu.java.lab2.messenger.entities.CategoryUsers.FRIEND;
import static ua.sumdu.java.lab2.messenger.entities.CategoryUsers.BLACKLIST;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.TreeMap;
import org.junit.Test;
import ua.sumdu.java.lab2.messenger.entities.CategoryUsers;
import ua.sumdu.java.lab2.messenger.entities.User;
import ua.sumdu.java.lab2.messenger.entities.UserMapImpl;

public class UserMapImplTest {
    @Test
    public void addUser() throws UnknownHostException {
        Map<String, User> map = new TreeMap<>();
        User user1 = new User(FRIEND, "user1", "user1@ex.so", 8080, InetAddress.getLocalHost());
        map.put(user1.getUsername(), user1);
        UserMapImpl usermap = new UserMapImpl();
        usermap.addUser(user1);
        assertTrue(usermap.getMap().equals(map));
        User user2 = new User(BLACKLIST, "user2", "user2@ex.so", 8080, InetAddress.getLocalHost());
        map.put(user2.getUsername(), user2);
        usermap.addUser(user2);
        assertTrue(usermap.getMap().equals(map));
    }

    @Test
    public void removeUser() throws UnknownHostException {
        Map<String, User> map = new TreeMap<>();
        UserMapImpl usermap = new UserMapImpl();
        User user1 = new User(FRIEND, "user1", "user1@ex.so", 8080, InetAddress.getLocalHost());
        usermap.addUser(user1);
        User user2 = new User(BLACKLIST, "user2", "user2@ex.so", 8080, InetAddress.getLocalHost());
        usermap.addUser(user2);
        User user3 = new User(BLACKLIST, "user3", "user3@ex.so", 8080, InetAddress.getLocalHost());
        usermap.addUser(user3);
        User user4 = new User(BLACKLIST, "user4", "user4@ex.so", 8080, InetAddress.getLocalHost());
        usermap.addUser(user4);
        User user5 = new User(BLACKLIST, "user5", "user5@ex.so", 8080, InetAddress.getLocalHost());
        usermap.addUser(user5);
        map.put(user2.getUsername(), user2);
        map.put(user3.getUsername(), user3);
        map.put(user4.getUsername(), user4);
        map.put(user5.getUsername(), user5);
        usermap.removeUser(user1);
        assertTrue(usermap.getMap().equals(map));
        map.remove(user5.getUsername());
        usermap.removeUser(user5);
        assertTrue(usermap.getMap().equals(map));
        map.remove(user3.getUsername());
        usermap.removeUser(user3);
        assertTrue(usermap.getMap().equals(map));
    }

    @Test
    public void equalsAndHashcode() throws UnknownHostException {
        User newUser = new User(CategoryUsers.FRIEND, "user1", "user1@ex.ex",
                7401, InetAddress.getLocalHost());
        UserMapImpl userMap1 = new UserMapImpl();
        UserMapImpl userMap2 = new UserMapImpl();
        userMap1.addUser(newUser);
        userMap2.addUser(newUser);
        assertTrue(userMap1.equals(userMap2));
        assertTrue(userMap1.hashCode() == userMap2.hashCode());
    }

}