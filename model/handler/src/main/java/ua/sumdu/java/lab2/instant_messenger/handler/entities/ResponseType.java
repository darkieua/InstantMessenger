package ua.sumdu.java.lab2.instant_messenger.handler.entities;


public enum ResponseType {
    SUCCESSFUL(100),
    USER_IS_OFFLINE(201),
    REQUEST_HAS_BEEN_DECLINED(301),
    UPDATED_GROUP_LIST(401),
    REQUESTED_MESSAGES(402),
    ADDED_TO_FRIENDS(501),
    ADDED_TO_GROUP(502),
    UNIDENTIFIED_REQUEST(666);

    public int getResponseNumber() {
        return responseNumber;
    }

    private int responseNumber;

    ResponseType(int responseNumber) {
        this.responseNumber = responseNumber;
    }
}
