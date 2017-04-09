package ua.sumdu.java.lab2.instant_messenger.handler.entities;

public enum RequestType {
    ADD_TO_FRIENDS(10),
    ADD_TO_GROUP(20),
    NEW_MESSAGE(31),
    NEW_MESSAGE_WITH_FILES_TO_FRIEND(32),
    NEW_MESSAGE_TO_GROUP(33),
    NEW_MESSAGE_WITH_FILES_TO_GROUP(34),
    UPDATE_GROUP_LIST(41),
    REQUEST_FOR_UPDATE_GROUP_LIST(42),
    MESSAGES_FROM_A_SPECIFIC_DATE(51);

    public int getRequestNumber() {
        return requestNumber;
    }

    private int requestNumber;

    RequestType(int requestNumber) {
        this.requestNumber = requestNumber;
    }
}
