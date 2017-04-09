package ua.sumdu.java.lab2.instant_messenger.handler.processing;

import javax.xml.transform.TransformerException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws TransformerException, IOException {
        //Message mess = new Message("user1", "user2", "dssf", LocalDateTime.now());
        String str = "30 ";
        System.out.println(Integer.parseInt(str));
    }
}
