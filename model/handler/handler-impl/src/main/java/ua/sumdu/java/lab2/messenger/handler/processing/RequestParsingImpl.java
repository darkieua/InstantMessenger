package ua.sumdu.java.lab2.messenger.handler.processing;

import static ua.sumdu.java.lab2.messenger.handler.entities.ResponseType.*;

import ua.sumdu.java.lab2.messenger.handler.api.RequestParsing;

public class RequestParsingImpl implements RequestParsing {
    @Override
    public String requestParsing(String string) {
        int requestType = Integer.parseInt(string.substring(0,4));
        String context = string.substring(5);
        if (requestType > 999 && requestType <= 2999) {
            return AddingRequestParsing.adding(requestType, context);
        } else if ((requestType > 2999 && requestType <= 6999)) {
            return newInfoRequest(requestType, context);
        } else if (requestType > 3999 && requestType <= 5999) {
            return UpdateRequestParsing.updateRequestsAndUpdateData(requestType, context);
        } else if (requestType > 6999 && requestType <= 7999) {
            return DeleteRequestParsing.deleteRequests(requestType, context);
        } else {
            return String.valueOf(UNIDENTIFIED_REQUEST.getResponseNumber());
        }
    }

    private String newInfoRequest(int requestType, String context) {
        if ((requestType > 2999 && requestType <= 3999)
                || (requestType > 5999 && requestType <= 6999)) {
            return DataAndMessageRequestParsing.receivingDataAndMessages(requestType, context);
        } else if (requestType > 3999 && requestType <= 5999) {
            return UpdateRequestParsing.updateRequestsAndUpdateData(requestType, context);
        } else {
            return String.valueOf(UNIDENTIFIED_REQUEST.getResponseNumber());
        }
    }
}
