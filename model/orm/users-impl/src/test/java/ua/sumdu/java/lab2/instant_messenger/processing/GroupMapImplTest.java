package ua.sumdu.java.lab2.instant_messenger.processing;

import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import ua.sumdu.java.lab2.instant_messenger.entities.GroupMapImpl;
import ua.sumdu.java.lab2.instant_messenger.entities.UserMapImpl;
import ua.sumdu.java.lab2.instant_messenger.entities.User;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.junit.Test;
import org.junit.runner.RunWith;

import static ua.sumdu.java.lab2.instant_messenger.entities.CategoryUsers.FRIEND;
import static ua.sumdu.java.lab2.instant_messenger.entities.CategoryUsers.BLACKLIST;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

@RunWith(DataProviderRunner.class)
public class GroupMapImplTest {

    private static final String  USER1 = "user1";

    @DataProvider
    public static Object[][] data() throws UnknownHostException {
        User[] users = {new User(FRIEND, USER1, USER1 + "@ex.so", 8080, InetAddress.getLocalHost()),
                new User(BLACKLIST, "user2", "user2@ex.so", 8080, InetAddress.getLocalHost()),
                new User(BLACKLIST, "user3", "user3@ex.so", 8080, InetAddress.getLocalHost()),
                new User(BLACKLIST, "user4", "user4@ex.so", 8080, InetAddress.getLocalHost()),
                new User(BLACKLIST, "user5", "user5@ex.so", 8080, InetAddress.getLocalHost())};
        UserMapImpl userMap = new UserMapImpl();
        String nameChat = "main1";
        for (User user : users) {
            userMap.addUser(user);
        }
        Map<String, UserMapImpl> map = new TreeMap<>();
        map.put(nameChat, userMap);
        return new Object[][]{{map, nameChat, userMap}};
    }

    @DataProvider
    public static Object[][] dataForDelete() throws UnknownHostException {
        Object[][] obj = data();
        Map<String, UserMapImpl> map0 = (Map<String, UserMapImpl>) obj[0][0];
        Map<String, UserMapImpl> map = (Map<String, UserMapImpl>) obj[0][0];
        GroupMapImpl group0 = new GroupMapImpl();
        group0.setMap(map0);
        GroupMapImpl group = new GroupMapImpl();
        group.setMap(map);
        String name = (String) obj[0][1];
        UserMapImpl userMap = (UserMapImpl) obj[0][2];
        Object[][] res = new Object[4][4];
        User user1 = userMap.getMap().get(USER1);
        map.get(name).getMap().remove(USER1);
        res[0] = new Object[]{group0, group, name, user1};
        User user3 = userMap.getMap().get("user3");
        User user5 = userMap.getMap().get("user5");
        GroupMapImpl group1 = new GroupMapImpl();
        GroupMapImpl group2 = new GroupMapImpl();
        Map<String, UserMapImpl> map1 = new TreeMap<>();
        map1.putAll(map);
        map1.remove("user5");
        Map<String, UserMapImpl> map2 = new TreeMap<>();
        map2.putAll(map1);
        map2.remove("user3");
        group1.setMap(map1);
        group2.setMap(map2);
        res[1] = new Object[]{group, group1, name, user5};
        res[2] = new Object[]{group1, group2, name, user3};
        res[3] = new Object[]{group2, group2, name, User.getEmptyUser()};
        return res;
    }

    @Test
    @UseDataProvider("data")
    public void addUser(Map<String, UserMapImpl> map, String nameChat, UserMapImpl users) throws UnknownHostException {
        GroupMapImpl mapForGroup = new GroupMapImpl();
        for (User user :users.getMap().values()) {
            mapForGroup.addUser(nameChat, user);
        }
        assertTrue(map.equals(mapForGroup.getMap()));
    }

    @Test
    @UseDataProvider("dataForDelete")
    public void deleteUser(GroupMapImpl current, GroupMapImpl result, String name, User user) throws UnknownHostException {
        current.deleteUser(name, user);
        assertTrue(result.equals(current));
    }

    @DataProvider
    public static Object[] dataForEquals() throws UnknownHostException {
        Object[][] obj = data();
        Map<String, UserMapImpl> group = (Map<String, UserMapImpl>) obj[0][0];
        GroupMapImpl groupMap = new GroupMapImpl();
        groupMap.setMap(group);
        String name = (String) obj[0][1];
        GroupMapImpl newGroup = new GroupMapImpl();
        Set<Map.Entry<String, UserMapImpl>> mainSet = group.entrySet();
        for (Map.Entry<String, UserMapImpl> entry :mainSet) {
            for (User user : entry.getValue().getMap().values()) {
                newGroup.addUser(entry.getKey(), user);
            }
        }
        User user = group.get(name).getMap().get(USER1);
        newGroup.deleteUser(name, user);
        return new Object[][] {{newGroup, groupMap}};
    }

    @Test
    @UseDataProvider("dataForEquals")
    public void equalsTest(GroupMapImpl groups, GroupMapImpl newGroups) throws UnknownHostException {
        assertTrue(groups.equals(groups));
        assertFalse(newGroups.equals(groups));
    }
}