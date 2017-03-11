package ua.sumdu.java.lab2.instant_messenger.processing;

import ua.sumdu.java.lab2.instant_messenger.api.CreateUser;
import ua.sumdu.java.lab2.instant_messenger.api.UserMap;
import ua.sumdu.java.lab2.instant_messenger.entities.CategoryUsers;
import ua.sumdu.java.lab2.instant_messenger.entities.User;

import java.util.Map;
import java.util.TreeMap;
import org.junit.Assert;
import org.junit.Test;

public class UserMapImplTest {
    @Test
    public void addUser() throws Exception {
        Map<String, User> map = new TreeMap<>();
        CreateUser creater = CreateUserImpl.getInstance();
        User user1 = creater.createUser(CategoryUsers.FRIEND, "user1", "user1@ex.so", "193.168.1.1", 8080);
        User user2 = creater.createUser(CategoryUsers.BLACKLIST, "user2", "user2@ex.so", "193.168.1.1", 8080);
        map.put(user1.getUsername(), user1);
        UserMap usermap = new UserMapImpl();
        usermap.addUser(user1);
        Assert.assertTrue(usermap.equals(map));
        map.put(user2.getUsername(), user2);
        usermap.addUser(user2);
        Assert.assertTrue(usermap.equals(map));
    }

    @Test
    public void removeUser() throws Exception {
        Map<String, User> map = new TreeMap<>();
        CreateUser creater = CreateUserImpl.getInstance();
        User user1 = creater.createUser(CategoryUsers.FRIEND, "user1", "user1@ex.so", "193.168.1.1", 8080);
        User user2 = creater.createUser(CategoryUsers.BLACKLIST, "user2", "user2@ex.so", "193.168.1.1", 8080);
        User user3 = creater.createUser(CategoryUsers.BLACKLIST, "user3", "user3@ex.so", "193.168.1.1", 8080);
        User user4 = creater.createUser(CategoryUsers.BLACKLIST, "user4", "user4@ex.so", "193.168.1.1", 8080);
        User user5 = creater.createUser(CategoryUsers.BLACKLIST, "user5", "user5@ex.so", "193.168.1.1", 8080);
        map.put(user2.getUsername(), user2);
        map.put(user3.getUsername(), user3);
        map.put(user4.getUsername(), user4);
        map.put(user5.getUsername(), user5);
        UserMap usermap = new UserMapImpl();
        usermap.addUser(user1);
        usermap.addUser(user2);
        usermap.addUser(user3);
        usermap.addUser(user4);
        usermap.addUser(user5);
        usermap.removeUser(user1);
        Assert.assertTrue(usermap.equals(map));
        map.remove(user5.getUsername());
        usermap.removeUser(user5);
        Assert.assertTrue(usermap.equals(map));
        map.remove(user3.getUsername());
        usermap.removeUser(user3);
        Assert.assertTrue(usermap.equals(map));
    }

}