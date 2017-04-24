package ua.sumdu.java.lab2.messenger.handler.entities;

public enum RequestType {
    ADD_TO_FRIENDS(1000),
    ADD_TO_GROUP(2000),
    NEW_MESSAGE(3100),
    NEW_MESSAGE_TO_GROUP(3200),
    UPDATE_GROUP_LIST(4100),
    REQUEST_FOR_UPDATE_GROUP_LIST(4200),
    MESSAGES_FROM_A_SPECIFIC_DATE(5100),
    GROUP_MESSAGES_FROM_A_SPECIFIC_DATE(5200),
    DATA_REQUEST(6000),
    DATA_TRANSFER(6001),
    DATA_TRANSFER_TO_GROUP(6100),
    REMOVING_FROM_FRIENDS(7001),
    USER_LEFT_GROUP(7002);

    public int getRequestNumber() {
        return requestNumber;
    }

    private int requestNumber;

    RequestType(final int request) {
        this.requestNumber = request;
    }
}
