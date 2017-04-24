package ua.sumdu.java.lab2.messenger.processing;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static ua.sumdu.java.lab2.messenger.entities.CategoryUsers.FRIEND;
import static ua.sumdu.java.lab2.messenger.entities.CategoryUsers.BLACKLIST;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import ua.sumdu.java.lab2.messenger.api.GroupMap;
import ua.sumdu.java.lab2.messenger.api.UserMap;
import ua.sumdu.java.lab2.messenger.entities.GroupMapImpl;
import ua.sumdu.java.lab2.messenger.entities.UserMapImpl;
import ua.sumdu.java.lab2.messenger.entities.User;

@RunWith(DataProviderRunner.class)
public class GroupMapImplTest {

    private static final String USER1 = "user1";

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
        Map<String, UserMap> map0 = (Map<String, UserMap>) obj[0][0];
        Map<String, UserMap> map = (Map<String, UserMap>) obj[0][0];
        GroupMap group0 = new GroupMapImpl();
        group0.setMap(map0);
        GroupMapImpl group = new GroupMapImpl();
        group.setMap(map);
        String name = (String) obj[0][1];
        UserMapImpl userMap = (UserMapImpl) obj[0][2];
        Object[][] res = new Object[4][4];
        User user1 = userMap.getMap().get(USER1);
        map.get(name).getMap().remove(USER1);
        res[0] = new Object[]{group0, group, name, user1};
        Map<String, UserMap> map1 = new TreeMap<>();
        map1.putAll(map);
        Map<String, UserMap> map2 = new TreeMap<>();
        map2.putAll(map1);
        User user5 = userMap.getMap().get("user5");
        User user3 = userMap.getMap().get("user3");
        map1.remove("user5");
        map2.remove("user3");
        GroupMapImpl group1 = new GroupMapImpl();
        group1.setMap(map1);
        GroupMapImpl group2 = new GroupMapImpl();
        group2.setMap(map2);
        res[1] = new Object[]{group, group1, name, user5};
        res[2] = new Object[]{group1, group2, name, user3};
        res[3] = new Object[]{group2, group2, name, User.getEmptyUser()};
        return res;
    }

    @Test
    @UseDataProvider("data")
    public void addUser(Map<String, UserMap> map, String nameChat, UserMapImpl users) throws UnknownHostException {
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
        Map<String, UserMap> group = (Map<String, UserMap>) obj[0][0];
        GroupMapImpl groupMap = new GroupMapImpl();
        groupMap.setMap(group);
        String name = (String) obj[0][1];
        GroupMapImpl newGroup = new GroupMapImpl();
        Set<Map.Entry<String, UserMap>> mainSet = group.entrySet();
        for (Map.Entry<String, UserMap> entry :mainSet) {
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