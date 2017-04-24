package ua.sumdu.java.lab2.messenger.handler.entities;


public enum ResponseType {
  SUCCESSFUL(1000),
  USER_IS_OFFLINE(2010),
  REQUEST_HAS_BEEN_DECLINED(3010),
  UPDATED_GROUP_LIST(4010),
  REQUESTED_MESSAGES(4020),
  REQUESTED_GROUP_MESSAGES(4030),
  ADDED_TO_FRIENDS(5010),
  ADDED_TO_GROUP(5020),
  UNIDENTIFIED_REQUEST(6660),
  DATA_ACQUISITION(7001),
  DATA_SENDING_REJECTED(7002);

  public int getResponseNumber() {
    return responseNumber;
  }

  private int responseNumber;

  ResponseType(final int response) {
    this.responseNumber = response;
  }
}
