package ua.sumdu.java.lab2.instant_messenger.processing;

import org.junit.Before;
import org.junit.Test;
import ua.sumdu.java.lab2.instant_messenger.api.GroupMap;
import ua.sumdu.java.lab2.instant_messenger.api.GroupMapParser;
import ua.sumdu.java.lab2.instant_messenger.api.UserMap;
import ua.sumdu.java.lab2.instant_messenger.api.UserMapParser;
import ua.sumdu.java.lab2.instant_messenger.common_entities.User;
import ua.sumdu.java.lab2.instant_messenger.entities.GroupMapImpl;
import ua.sumdu.java.lab2.instant_messenger.entities.UserMapImpl;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static ua.sumdu.java.lab2.instant_messenger.common_entities.CategoryUsers.FRIEND;
import static ua.sumdu.java.lab2.instant_messenger.common_entities.CategoryUsers.BLACKLIST;
import static org.junit.Assert.assertTrue;

public class ParsingTest {

    private UserMap userMap;
    private GroupMap groupMap;

    @Before
    public void set() throws UnknownHostException {
        User user1 = new User(FRIEND, "user1", "user1@ex.so", 8080, InetAddress.getLocalHost());
        User user2 = new User(BLACKLIST, "user2", "user2@ex.so", 8080, InetAddress.getLocalHost());
        User user3 = new User(BLACKLIST, "user3", "user3@ex.so", 8080, InetAddress.getLocalHost());
        User user4 = new User(BLACKLIST, "user4", "user4@ex.so", 8080, InetAddress.getLocalHost());
        User user5 = new User(BLACKLIST, "user5", "user5@ex.so", 8080, InetAddress.getLocalHost());
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
    public void userMapToJSonAndBack(){
        UserMapParser parser = UserMapParserImpl.getInstance();
        String str = parser.userMapToJSonString(userMap);
        UserMapImpl newUserMap = (UserMapImpl) parser.jsonStringToUserMap(str);
        assertTrue(newUserMap.equals(userMap));

    }

    @Test
    public void groupMapToJSonAndBack(){
        GroupMapParser parser = GroupMapParserImpl.getInstance();
        String str = parser.groupMapToJSonString(groupMap);
        GroupMapImpl newGroupMap = (GroupMapImpl) parser.jsonStringToGroupMap(str);
        assertTrue(newGroupMap.equals(groupMap));
    }

}