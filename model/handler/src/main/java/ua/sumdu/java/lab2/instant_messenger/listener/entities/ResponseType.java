package ua.sumdu.java.lab2.instant_messenger.listener.entities;


public enum ResponseType {
    SUCCESSFUL(100),
    USER_IS_OFFLINE(201),
    ADMIN_IS_OFFLINE(202),
    REQUEST_HAS_BEEN_DECLINED(301),
    RECEIVING_FILES_REJECTED(302),
    CURRENT_USER_IS_BLOCKED(303);

    public int getRequestNumber() {
        return responseNumber;
    }

    private int responseNumber;

    ResponseType(int responseNumber) {
        this.responseNumber = responseNumber;
    }
}
