package ua.sumdu.java.lab2.messenger.handler.test;

import static ua.sumdu.java.lab2.messenger.handler.entities.RequestType.*;

import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import ua.sumdu.java.lab2.messenger.entities.GroupMapImpl;
import ua.sumdu.java.lab2.messenger.entities.User;
import ua.sumdu.java.lab2.messenger.handler.processing.MessageRequestGeneratingImpl;
import ua.sumdu.java.lab2.messenger.handler.processing.RequestGeneratingImpl;
import ua.sumdu.java.lab2.messenger.processing.GroupMapParserImpl;

@RunWith(DataProviderRunner.class)
public class RequestGeneratingImplTest2 {

    private RequestGeneratingImpl requestGenerating;

    @Before
    public void init() {
        requestGenerating = new RequestGeneratingImpl();
    }

    @Test
    public void updateGroupList() {
        GroupMapImpl groups = (GroupMapImpl) GroupMapParserImpl.getInstance().getGroupMap();
        String chatName = "testGroup";
        groups.addUser(chatName, User.getEmptyUser());
        GroupMapParserImpl.getInstance().writeGroupMapToFile(GroupMapParserImpl.getInstance()
            .groupMapToJSonString(groups));
        String result = requestGenerating.updateGroupList(chatName);
        GroupMapImpl groupMap = new GroupMapImpl();
        groupMap.addUser(chatName, User.getEmptyUser());
        String correctRequest = UPDATE_GROUP_LIST.getRequestNumber() + "=" + GroupMapParserImpl
            .getInstance().groupMapToJSonString(groupMap);
        Assert.assertEquals(RequestParsingImplTest.getMessage(result, correctRequest), correctRequest, result);
        groups.getMap().remove(chatName);
        GroupMapParserImpl.getInstance().writeGroupMapToFile(GroupMapParserImpl.getInstance()
            .groupMapToJSonString(groups));
    }

    @Test
    public void requestForUpdateGroupList() {
        String chatName = "testGroup";
        String result = requestGenerating.createRequestForUpdateGroupList(chatName);
        String correctRequest = REQUEST_FOR_UPDATE_GROUP_LIST.getRequestNumber() + "=" + chatName;
        Assert.assertEquals(RequestParsingImplTest.getMessage(result, correctRequest), correctRequest, result);
    }

    @Test
    public void messagesFromSpecificDate() {
        long date = 1;
        String result = new MessageRequestGeneratingImpl().createRequestForMessagesFromSpecificDate(date);
        String correctRequest = MESSAGES_FROM_A_SPECIFIC_DATE.getRequestNumber() + "=" + date + "="
            + User.getCurrentUser().getUsername();
        Assert.assertEquals(RequestParsingImplTest.getMessage(result, correctRequest), correctRequest, result);
    }

    @Test
    public void groupMessageFromSpecificDate() {
        long date = 1;
        String groupName = "main";
        String result = new MessageRequestGeneratingImpl().createRequestForGroupMessagesFromSpecificDate(date, groupName);
        String correctRequest = GROUP_MESSAGES_FROM_A_SPECIFIC_DATE.getRequestNumber() + "=" + date + "="
                + groupName;
        Assert.assertEquals(RequestParsingImplTest.getMessage(result, correctRequest), correctRequest, result);
    }
}
