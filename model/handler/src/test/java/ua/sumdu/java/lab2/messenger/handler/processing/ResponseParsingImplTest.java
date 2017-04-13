package ua.sumdu.java.lab2.messenger.handler.processing;

import static ua.sumdu.java.lab2.messenger.handler.entities.ResponseType.*;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(DataProviderRunner.class)
public class ResponseParsingImplTest {

  ResponseParsingImpl responseParsing;

  @Before
  public void init() {
    responseParsing = new ResponseParsingImpl();
  }

  @DataProvider
  public static Object[][] shortResponsesWithoutParsing() {
    return new Object[][]{{String.valueOf(SUCCESSFUL.getResponseNumber())}, {String.valueOf(SUCCESSFUL.getResponseNumber())}};
  }

  @Test
  @UseDataProvider("shortResponsesWithoutParsing")
  public void nonProcessingShortResponses(String request) throws Exception {
    //String result;// = responseParsing.responseParsing(request);
    //assertEquals(RequestParsingImplTest.getMessage(result, request), result, request);
  }

}