package ua.sumdu.java.lab2.instant_messenger.handler.entities;

public enum RequestType {
    ADD_TO_FRIENDS(1000),
    ADD_TO_GROUP(2000),
    NEW_MESSAGE(3100),
    NEW_MESSAGE_TO_GROUP(3300),
    UPDATE_GROUP_LIST(4100),
    REQUEST_FOR_UPDATE_GROUP_LIST(4200),
    MESSAGES_FROM_A_SPECIFIC_DATE(5100),
    GROUP_MESSAGES_FROM_A_SPECIFIC_DATE(5200),//new
    DATA_TRANSFER(6000),//new
    DATA_TRANSFER_TO_GROUP(6100);//new

    public int getRequestNumber() {
        return requestNumber;
    }

    private int requestNumber;

    RequestType(int requestNumber) {
        this.requestNumber = requestNumber;
    }
}
