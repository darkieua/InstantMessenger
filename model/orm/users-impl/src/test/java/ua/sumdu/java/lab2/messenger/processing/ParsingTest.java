package ua.sumdu.java.lab2.messenger.processing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ua.sumdu.java.lab2.messenger.entities.CategoryUsers.FRIEND;
import static ua.sumdu.java.lab2.messenger.entities.CategoryUsers.BLACKLIST;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.junit.Before;
import org.junit.Test;
import ua.sumdu.java.lab2.messenger.api.GroupMap;
import ua.sumdu.java.lab2.messenger.api.GroupMapParser;
import ua.sumdu.java.lab2.messenger.api.UserMap;
import ua.sumdu.java.lab2.messenger.api.UserMapParser;
import ua.sumdu.java.lab2.messenger.entities.User;
import ua.sumdu.java.lab2.messenger.entities.GroupMapImpl;
import ua.sumdu.java.lab2.messenger.entities.UserMapImpl;

public class ParsingTest {

    private UserMap userMap;
    private GroupMap groupMap;

    /**
     * Filling test data.
     */
    @Before
    public void set() throws UnknownHostException {
        userMap = new UserMapImpl();
        groupMap = new GroupMapImpl();
        User user1 = new User(FRIEND, "user1", "user1@ex.so", 8080, InetAddress.getLocalHost());
        userMap.addUser(user1);
        groupMap.addUser("main", user1);
        User user2 = new User(BLACKLIST, "user2", "user2@ex.so", 8080, InetAddress.getLocalHost());
        userMap.addUser(user2);
        groupMap.addUser("main", user2);
        User user3 = new User(BLACKLIST, "user3", "user3@ex.so", 8080, InetAddress.getLocalHost());
        userMap.addUser(user3);
        groupMap.addUser("main", user3);
        User user4 = new User(BLACKLIST, "user4", "user4@ex.so", 8080, InetAddress.getLocalHost());
        groupMap.addUser("other", user4);
        User user5 = new User(BLACKLIST, "user5", "user5@ex.so", 8080, InetAddress.getLocalHost());
        groupMap.addUser("other", user5);
    }

    @Test
    public void userMapToJSonAndBack() {
        UserMapParser parser = UserMapParserImpl.getInstance();
        String str = parser.userMapToJSonString(userMap);
        UserMapImpl newUserMap = (UserMapImpl) parser.jsonStringToUserMap(str);
        assertTrue(newUserMap.equals(userMap));

    }

    @Test
    public void groupMapToJSonAndBack() {
        GroupMapParser parser = GroupMapParserImpl.getInstance();
        String str = parser.groupMapToJSonString(groupMap);
        GroupMapImpl newGroupMap = (GroupMapImpl) parser.jsonStringToGroupMap(str);
        assertTrue(newGroupMap.equals(groupMap));
    }

 /* @Test
    public void gerCurrentGroups() {
        GroupMapImpl groups = (GroupMapImpl) GroupMapParserImpl.getInstance().getGroupMap();
        User newUser = User.getEmptyUser();
        groups.addUser("test", newUser);
        GroupMapParserImpl.getInstance().writeGroupMapToFile(GroupMapParserImpl.getInstance().groupMapToJSonString(groups));
        GroupMapImpl newGroup = (GroupMapImpl) GroupMapParserImpl.getInstance().getGroupMap();
        assertEquals(groups, newGroup);
        UserMapImpl userMap = (UserMapImpl) GroupMapParserImpl.getInstance().getUserMap("test");
        UserMapImpl correctUserMap = new UserMapImpl();
        correctUserMap.addUser(newUser);
        assertEquals(userMap, correctUserMap);
        newGroup.getMap().remove("test");
        GroupMapParserImpl.getInstance().writeGroupMapToFile(GroupMapParserImpl.getInstance().groupMapToJSonString(newGroup));
    }*/

    @Test
    public void getEmptyGroups() {
        GroupMapImpl groupMap = (GroupMapImpl) GroupMapParserImpl.getInstance().jsonStringToGroupMap("");
        assertEquals(groupMap, new GroupMapImpl());
    }

}