package ua.sumdu.java.lab2.messenger.parsers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ua.sumdu.java.lab2.messenger.api.MessageMap;
import ua.sumdu.java.lab2.messenger.entities.Message;
import ua.sumdu.java.lab2.messenger.entities.MessageMapImpl;

public class ParsingMessages {

    private static final Logger LOG = LoggerFactory.getLogger(ParsingMessages.class);

    /**
     *Selecting from the document messages that were sent later than some time.
     */

    public static MessageMap getMessagesFromSpecificDate(Document doc, long date) {
        XPathFactory pathFactory = XPathFactory.newInstance();
        XPath xpath = pathFactory.newXPath();
        XPathExpression expr;
        if (Objects.isNull(doc.getDocumentElement())) {
            return new MessageMapImpl();
        }
        try {
            expr = xpath.compile("messages/message[@date>"
                    + date + "]");
            NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            MessageMapImpl map = new MessageMapImpl();
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                map.addMessage(parseMessage(node));
            }
            return map;
        } catch (XPathExpressionException e) {
            LOG.error(e.getMessage(), e);
            return new MessageMapImpl();
        }
    }

    /**
     * Method parses the messages of the current node.
     */

    public static Message parseMessage(Node node) {
        if (node.getNodeType() == node.ELEMENT_NODE) {
            Element elem = (Element) node;
            long countMilliSeconds = Long.parseLong(elem.getAttribute("date"));
            LocalDateTime date =
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(countMilliSeconds),
                            ZoneId.systemDefault());
            String text = elem.getElementsByTagName("text").item(0).getTextContent();
            String senderUsername = elem.getElementsByTagName("senderUsername").item(0)
                    .getTextContent();
            String receiverUsername = elem.getElementsByTagName("receiverUsername").item(0)
                    .getTextContent();
            return new Message(senderUsername, receiverUsername, text, date);
        } else {
            return null;
        }
    }

}
