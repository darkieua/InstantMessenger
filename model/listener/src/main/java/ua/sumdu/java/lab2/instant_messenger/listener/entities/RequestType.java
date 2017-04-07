package ua.sumdu.java.lab2.instant_messenger.listener.entities;

public enum RequestType {
    ADD_TO_FRIENDS(10),
    ADD_TO_GROUP(21),
    JOIN_THE_GROUP(22),
    NEW_MESSAGE(31),
    SENDING_FILES_TO_FRIEND(32),
    SENDING_FILES_TO_GROUP(33);

    public int getRequestNumber() {
        return requestNumber;
    }

    private int requestNumber;

    RequestType(int requestNumber) {
        this.requestNumber = requestNumber;
    }
}
