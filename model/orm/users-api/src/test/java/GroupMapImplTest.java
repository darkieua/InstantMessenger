import ua.sumdu.java.lab2.instant_messenger.api.GroupMap;
import ua.sumdu.java.lab2.instant_messenger.api.UserMap;
import ua.sumdu.java.lab2.instant_messenger.entities.GroupMapImpl;
import ua.sumdu.java.lab2.instant_messenger.entities.UserMapImpl;
import ua.sumdu.java.lab2.instant_messenger.common_entities.User;

import java.util.Map;
import java.util.TreeMap;
import org.junit.Test;

import static ua.sumdu.java.lab2.instant_messenger.common_entities.CategoryUsers.FRIEND;
import static ua.sumdu.java.lab2.instant_messenger.common_entities.CategoryUsers.BLACKLIST;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class GroupMapImplTest {
    @Test
    public void addUser() throws Exception {
        User user1 = new User();
        user1.update(FRIEND, "user1", "user1@ex.so", "193.168.1.1", 8080);
        User user2 = new User();
        user2.update(BLACKLIST, "user2", "user2@ex.so", "193.168.1.1", 8080);
        UserMap userMap = new UserMapImpl();
        userMap.addUser(user1);
        Map<String, UserMap> map = new TreeMap<>();
        String nameChat = "main1";
        userMap.addUser(user2);
        map.put(nameChat, userMap);
        GroupMapImpl mapForGroup = new GroupMapImpl();
        mapForGroup.addUser(nameChat, user1);
        mapForGroup.addUser(nameChat, user2);
        assertTrue(mapForGroup.equals(map));
    }

    @Test
    public void deleteUser() throws Exception {
        User user1 = new User();
        user1.update(FRIEND, "user1", "user1@ex.so", "193.168.1.1", 8080);
        User user2 = new User();
        user2.update(BLACKLIST, "user2", "user2@ex.so", "193.168.1.1", 8080);
        User user3 = new User();
        user3.update(BLACKLIST, "user3", "user3@ex.so", "193.168.1.1", 8080);
        User user4 = new User();
        user4.update(BLACKLIST, "user4", "user4@ex.so", "193.168.1.1", 8080);
        User user5 = new User();
        user5.update(BLACKLIST, "user5", "user5@ex.so", "193.168.1.1", 8080);
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
        assertTrue(mapForGroup.equals(map));
        map.get(nameChat).removeUser(user5);
        mapForGroup.deleteUser(nameChat, user5);
        assertTrue(mapForGroup.equals(map));
        map.get(nameChat).removeUser(user3);
        mapForGroup.deleteUser(nameChat, user3);
        assertTrue(mapForGroup.equals(map));
        map.get(nameChat).removeUser(user5);
        mapForGroup.deleteUser("sdf", user2);
        assertTrue(mapForGroup.equals(map));
        User user6 = new User();
        user6.update(BLACKLIST, "user6", "user6@ex.so", "193.168.1.1", 8080);
        mapForGroup.deleteUser(nameChat, user6);
        assertTrue(mapForGroup.equals(map));
    }

    @Test
    public void equals() {
        GroupMap mapForGroup = new GroupMapImpl();
        assertFalse(mapForGroup.equals(null));
        assertTrue(mapForGroup.equals(mapForGroup));
        Map<String, UserMap> map = new TreeMap<>();
        User user1 = new User();
        user1.update(FRIEND, "user1", "user1@ex.so", "193.168.1.1", 8080);
        UserMap usmap = new UserMapImpl();
        usmap.addUser(user1);
        map.put(user1.getUsername(), usmap);
        mapForGroup.addUser(user1.getUsername(), user1);
        assertTrue(mapForGroup.equals(map));
        User user2 = new User();
        user2.update(BLACKLIST, "user2", "user2@ex.so", "193.168.1.1", 8080);
        User user3 = new User();
        user3.update(BLACKLIST, "user3", "user3@ex.so", "193.168.1.1", 8080);
        map.get(user1.getUsername()).addUser(user2);
        mapForGroup.addUser(user1.getUsername(), user3);
        assertFalse(mapForGroup.equals(map));
    }
}