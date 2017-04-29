package ua.sumdu.java.lab2.messenger.handler.test;

import org.junit.Assert;
import org.junit.Test;
import ua.sumdu.java.lab2.messenger.entities.CategoryUsers;
import ua.sumdu.java.lab2.messenger.entities.GroupMapImpl;
import ua.sumdu.java.lab2.messenger.entities.User;
import ua.sumdu.java.lab2.messenger.handler.processing.ResponseGeneratingImpl;
import ua.sumdu.java.lab2.messenger.processing.GroupMapParserImpl;

import static ua.sumdu.java.lab2.messenger.handler.entities.ResponseType.ADDED_TO_FRIENDS;
import static ua.sumdu.java.lab2.messenger.handler.entities.ResponseType.ADDED_TO_GROUP;
import static ua.sumdu.java.lab2.messenger.handler.entities.ResponseType.REQUEST_HAS_BEEN_DECLINED;

public class ResponseGeneratingImplTest2 {

    @Test
    public void addedToFriend() {
        String request = String.valueOf(ADDED_TO_FRIENDS.getResponseNumber());
        String result = new ResponseGeneratingImpl().responseGenerate(request);
        String correctResult = request + "=" + User.getCurrentUser().setCategory(CategoryUsers.FRIEND).toJSonString();
        Assert.assertEquals(RequestParsingImplTest.getMessage(result, correctResult), result, correctResult);
    }


    @Test
    public void addedToGroup() {
        String request = ADDED_TO_GROUP.getResponseNumber() + "=test_chat";
        String result = new ResponseGeneratingImpl().responseGenerate(request);
        GroupMapImpl thisUser = new GroupMapImpl();
        thisUser.addUser("test_chat", User.getCurrentUser().setCategory(CategoryUsers.VISITOR));
        String correctResult = ADDED_TO_GROUP.getResponseNumber() + "=" + GroupMapParserImpl.getInstance().groupMapToJSonString(thisUser);
        Assert.assertEquals(RequestParsingImplTest.getMessage(result, correctResult), result, correctResult);

    }


    @Test
    public void declinedRequest() {
        String request = String.valueOf(REQUEST_HAS_BEEN_DECLINED.getResponseNumber());
        String result = new ResponseGeneratingImpl().responseGenerate(request);
        String correctResult = request + "=" + User.getCurrentUser().getUsername() + "(" + User.getCurrentUser().getIpAddress() + ")";
        Assert.assertEquals(RequestParsingImplTest.getMessage(result, correctResult), result, correctResult);
    }
}
