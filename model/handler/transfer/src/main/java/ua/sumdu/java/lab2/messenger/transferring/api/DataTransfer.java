package ua.sumdu.java.lab2.messenger.transferring.api;

import ua.sumdu.java.lab2.messenger.entities.SentFiles;

public interface DataTransfer {

    String dataRequest(SentFiles files);

    String requestParsing(String context);

    String dataAcquisition(String response);

    String parsingDataAcquisitionResponse(String context);

    String parsingDataSendingRejectedResponse(String context);
}
