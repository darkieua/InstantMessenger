package ua.sumdu.java.lab2.instant_messenger.processing;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ua.sumdu.java.lab2.instant_messenger.api.GroupMap;
import ua.sumdu.java.lab2.instant_messenger.api.UserMap;
import ua.sumdu.java.lab2.instant_messenger.entities.CategoryUsers;
import ua.sumdu.java.lab2.instant_messenger.entities.User;

public class ParsingTest {

    private static UserMap userMap;
    private static GroupMap groupMap;
    private static Parsing parser;

    @Before
    public static void set() {
        parser = Parsing.getInstance();
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
        userMap = new UserMapImpl();
        groupMap = new GroupMapImpl();
        userMap.addUser(user1);
        userMap.addUser(user2);
        userMap.addUser(user3);
        groupMap.addUser("main", user1);
        groupMap.addUser("main", user2);
        groupMap.addUser("main", user3);
        groupMap.addUser("other", user4);
        groupMap.addUser("other", user5);
    }

    @Test
    public void userMapToJSonAndBack() throws Exception {
        String str = parser.userMapToJSonString(userMap);
        UserMapImpl newUserMap = (UserMapImpl) parser.jsonStringToUserMap(str);
        Assert.assertEquals(newUserMap, userMap);

    }

    @Test
    public void groupMapToJSonAndBack() throws Exception {
        String str = parser.groupMapToJSonString(groupMap);
        GroupMapImpl newGroupMap = (GroupMapImpl) parser.jsonStringToGroupMap(str);
        Assert.assertEquals(newGroupMap, groupMap);
    }

}