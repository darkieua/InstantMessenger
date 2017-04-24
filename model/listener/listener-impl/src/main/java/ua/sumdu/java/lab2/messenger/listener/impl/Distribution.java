package ua.sumdu.java.lab2.messenger.listener.impl;

import ua.sumdu.java.lab2.messenger.entities.User;
import ua.sumdu.java.lab2.messenger.entities.UserMapImpl;
import ua.sumdu.java.lab2.messenger.handler.processing.RequestGeneratingImpl;
import ua.sumdu.java.lab2.messenger.processing.GroupMapParserImpl;

public class Distribution {
    public static void sendOutNewGroupList(String newUserUsername, String groupName) {
        UserMapImpl group = (UserMapImpl) GroupMapParserImpl.getInstance().getUserMap(groupName);
        RequestGeneratingImpl requestGenerating = new RequestGeneratingImpl();
        String request = requestGenerating.updateGroupList(groupName);
        for (User user : group.getMap().values()) {
            if (!newUserUsername.equals(user.getUsername())) {
                ClientImpl client = new ClientImpl(user.getIpAddress(), user.getPort(), request);
                client.run();
            }
        }
    }
}
