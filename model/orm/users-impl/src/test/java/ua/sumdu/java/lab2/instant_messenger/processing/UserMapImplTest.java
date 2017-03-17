package ua.sumdu.java.lab2.instant_messenger.processing;

import ua.sumdu.java.lab2.instant_messenger.entities.User;
import ua.sumdu.java.lab2.instant_messenger.entities.UserMapImpl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.TreeMap;
import org.junit.Test;

import static ua.sumdu.java.lab2.instant_messenger.entities.CategoryUsers.FRIEND;
import static ua.sumdu.java.lab2.instant_messenger.entities.CategoryUsers.BLACKLIST;
import static org.junit.Assert.assertTrue;

public class UserMapImplTest {
    @Test
    public void addUser() throws UnknownHostException {
        Map<String, User> map = new TreeMap<>();
        User user1 = new User(FRIEND, "user1", "user1@ex.so", 8080, InetAddress.getLocalHost());
        User user2 = new User(BLACKLIST, "user2", "user2@ex.so", 8080, InetAddress.getLocalHost());
        map.put(user1.getUsername(), user1);
        UserMapImpl usermap = new UserMapImpl();
        usermap.addUser(user1);
        assertTrue(usermap.getMap().equals(map));
        map.put(user2.getUsername(), user2);
        usermap.addUser(user2);
        assertTrue(usermap.getMap().equals(map));
    }

    @Test
    public void removeUser() throws UnknownHostException {
        Map<String, User> map = new TreeMap<>();
        User user1 = new User(FRIEND, "user1", "user1@ex.so", 8080, InetAddress.getLocalHost());
        User user2 = new User(BLACKLIST, "user2", "user2@ex.so", 8080,  InetAddress.getLocalHost());
        User user3 = new User(BLACKLIST, "user3", "user3@ex.so", 8080,  InetAddress.getLocalHost());
        User user4 = new User(BLACKLIST, "user4", "user4@ex.so", 8080,  InetAddress.getLocalHost());
        User user5 = new User(BLACKLIST, "user5", "user5@ex.so", 8080,  InetAddress.getLocalHost());
        map.put(user2.getUsername(), user2);
        map.put(user3.getUsername(), user3);
        map.put(user4.getUsername(), user4);
        map.put(user5.getUsername(), user5);
        UserMapImpl usermap = new UserMapImpl();
        usermap.addUser(user1);
        usermap.addUser(user2);
        usermap.addUser(user3);
        usermap.addUser(user4);
        usermap.addUser(user5);
        usermap.removeUser(user1);
        assertTrue(usermap.getMap().equals(map));
        map.remove(user5.getUsername());
        usermap.removeUser(user5);
        assertTrue(usermap.getMap().equals(map));
        map.remove(user3.getUsername());
        usermap.removeUser(user3);
        assertTrue(usermap.getMap().equals(map));
    }

}