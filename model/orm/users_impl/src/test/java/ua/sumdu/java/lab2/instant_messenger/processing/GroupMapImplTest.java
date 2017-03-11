package ua.sumdu.java.lab2.instant_messenger.processing;

import org.junit.Assert;
import org.junit.Test;
import ua.sumdu.java.lab2.instant_messenger.api.GroupMap;
import ua.sumdu.java.lab2.instant_messenger.api.UserMap;
import ua.sumdu.java.lab2.instant_messenger.entities.CategoryUsers;
import ua.sumdu.java.lab2.instant_messenger.entities.User;

import java.util.Map;
import java.util.TreeMap;

public class GroupMapImplTest {
    @Test
    public void addUser() throws Exception {
        User user1 = new User();
        user1.update(CategoryUsers.FRIEND, "user1", "user1@ex.so", "193.168.1.1", 8080);
        User user2 = new User();
        user2.update(CategoryUsers.BLACKLIST, "user2", "user2@ex.so", "193.168.1.1", 8080);
        UserMap userMap = new UserMapImpl();
        userMap.addUser(user1);
        Map<String, UserMap> map = new TreeMap<>();
        String nameChat = "main1";
        userMap.addUser(user2);
        map.put(nameChat, userMap);
        GroupMapImpl mapForGroup = new GroupMapImpl();
        mapForGroup.addUser(nameChat, user1);
        mapForGroup.addUser(nameChat, user2);
        Assert.assertTrue(mapForGroup.equals(map));
    }

    @Test
    public void deleteUser() throws Exception {
        User user1 = new User();
        user1.update(CategoryUsers.FRIEND, "user1", "user1@ex.so", "193.168.1.1", 8080);
        User user2 = new User();
        user2.update(CategoryUsers.BLACKLIST, "user2", "user2@ex.so", "193.168.1.1", 8080);
        User user3 = new User();
        user3.update(CategoryUsers.BLACKLIST, "user3", "user3@ex.so", "193.168.1.1", 8080);
        User user4 = new User();
        user4.update(CategoryUsers.BLACKLIST, "user4", "user4@ex.so", "193.168.1.1", 8080);
        User user5 = new User();
        user5.update(CategoryUsers.BLACKLIST, "user5", "user5@ex.so", "193.168.1.1", 8080);
        UserMap userMap = new UserMapImpl();
        userMap.addUser(user2);
        userMap.addUser(user3);
        userMap.addUser(user4);
        userMap.addUser(user5);
        Map<String, UserMap> map = new TreeMap<>();
        String nameChat = "main1";
        map.put(nameChat, userMap);
        GroupMap mapForGroup = new GroupMapImpl();
        mapForGroup.addUser(nameChat, user1);
        mapForGroup.addUser(nameChat, user2);
        mapForGroup.addUser(nameChat, user3);
        mapForGroup.addUser(nameChat, user4);
        mapForGroup.addUser(nameChat, user5);
        mapForGroup.deleteUser(nameChat, user1);
        Assert.assertTrue(mapForGroup.equals(map));
        map.get(nameChat).removeUser(user5);
        mapForGroup.deleteUser(nameChat, user5);
        Assert.assertTrue(mapForGroup.equals(map));
        map.get(nameChat).removeUser(user3);
        mapForGroup.deleteUser(nameChat, user3);
        Assert.assertTrue(mapForGroup.equals(map));
        map.get(nameChat).removeUser(user5);
        mapForGroup.deleteUser("sdf", user2);
        Assert.assertTrue(mapForGroup.equals(map));
        User user6 = new User();
        user6.update(CategoryUsers.BLACKLIST, "user6", "user6@ex.so", "193.168.1.1", 8080);
        mapForGroup.deleteUser(nameChat, user6);
        Assert.assertTrue(mapForGroup.equals(map));
    }

    @Test
    public void equals() {
        GroupMap mapForGroup = new GroupMapImpl();
        Assert.assertFalse(mapForGroup.equals(null));
        Assert.assertTrue(mapForGroup.equals(mapForGroup));
        Map<String, UserMap> map = new TreeMap<>();
        User user1 = new User();
        user1.update(CategoryUsers.FRIEND, "user1", "user1@ex.so", "193.168.1.1", 8080);
        UserMap usmap = new UserMapImpl();
        usmap.addUser(user1);
        map.put(user1.getUsername(), usmap);
        mapForGroup.addUser(user1.getUsername(), user1);
        Assert.assertTrue(mapForGroup.equals(map));
        User user2 = new User();
        user2.update(CategoryUsers.BLACKLIST, "user2", "user2@ex.so", "193.168.1.1", 8080);
        User user3 = new User();
        user3.update(CategoryUsers.BLACKLIST, "user3", "user3@ex.so", "193.168.1.1", 8080);
        map.get(user1.getUsername()).addUser(user2);
        mapForGroup.addUser(user1.getUsername(), user3);
        Assert.assertFalse(mapForGroup.equals(map));
    }
}