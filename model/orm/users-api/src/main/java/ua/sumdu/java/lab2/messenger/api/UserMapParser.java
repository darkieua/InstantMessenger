package ua.sumdu.java.lab2.messenger.api;

public interface UserMapParser {

    String userMapToJSonString(UserMap userMap);

    UserMap jsonStringToUserMap(String jsonString);

    boolean writeUserMapToFile(String jsonString);

    boolean writeBlackListToFile(String jsonString);

    UserMap getFriends();

    UserMap getBlackList();
}
