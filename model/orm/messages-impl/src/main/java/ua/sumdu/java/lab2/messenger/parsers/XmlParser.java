package ua.sumdu.java.lab2.messenger.parsers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import ua.sumdu.java.lab2.messenger.api.MessageMap;
import ua.sumdu.java.lab2.messenger.api.MessageMapParser;
import ua.sumdu.java.lab2.messenger.entities.Message;
import ua.sumdu.java.lab2.messenger.entities.MessageMapImpl;

public enum XmlParser implements MessageMapParser {

    INSTANCE;

    private static final Logger LOG = LoggerFactory.getLogger(XmlParser.class);

    /**
     * Method creates an item Document reading data with a file.
     * @return new document
     */

    public Document getDocument(File file) {
        try {
            if (!file.exists()) {
                file.createNewFile();
                return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            }
            if (file.length() == 0) {
                return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            }
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            try {
                return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            } catch (ParserConfigurationException e1) {
                LOG.error(e1.getMessage(), e1);
                return null;
            }
        }
    }

    /**
     *Method adds new messages to the document element.
     */

    public void addMessage(Element messages, Message mess, Document doc) {
        Element mes = doc.createElement("message");
        if (null == messages) {
            doc.appendChild(mes);
        } else {
            messages.appendChild(mes);
        }
        Date out = Date.from(mess.getTimeSending().atZone(ZoneId.systemDefault()).toInstant());
        long longTime = out.getTime();
        mes.setAttribute("date", String.valueOf(longTime));
        Element text = doc.createElement("text");
        mes.appendChild(text);
        text.setTextContent(mess.getText());
        Element sender = doc.createElement("senderUsername");
        mes.appendChild(sender);
        sender.setTextContent(mess.getSender());
        Element receiver = doc.createElement("receiverUsername");
        mes.appendChild(receiver);
        receiver.setTextContent(mess.getReceiver());
    }

    @Override
    public boolean write(MessageMap map, File file) {
        Document doc = writeMessageToDocument((MessageMapImpl) map, null);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
                return false;
            }
        }
        try (FileOutputStream fileWriter = new FileOutputStream(file)) {
            fileWriter.write(toXml(doc).getBytes());
            fileWriter.flush();
            fileWriter.close();
            return true;
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     *Write a message to a document. If the document is empty,
     * the document will be created.
     */

    public Document writeMessageToDocument(MessageMapImpl messageMap, Document doc) {
        Element root;
        Document document;
        if (null == doc) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            try {
                builder = factory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                LOG.error(e.getMessage(), e);
            }
            DOMImplementation impl = builder.getDOMImplementation();
            document = impl.createDocument(null, null, null);
            root = document.createElement("messages");
            document.appendChild(root);
        } else {
            document = doc;
            root = (Element)document.getFirstChild();
        }
        MessageMapImpl newMap = messageMap;
        for (Map.Entry<LocalDateTime, Message> messages : newMap.getMapForMails().entrySet()) {
            addMessage(root, messages.getValue(), document);
        }
        return document;
    }

    @Override
    public MessageMap read(File file) {
        Document doc = getDocument(file);
        long date = Date.from(LocalDateTime.now()
                .minusMonths(2)
                .atZone(ZoneId.systemDefault())
                .toInstant())
                .getTime();
        return ParsingMessages.getMessagesFromSpecificDate(doc, date);
    }

    /**
     * Converting a document to a xml string.
     */

    public String toXml(Document document) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(writer));
            return writer.getBuffer().toString().replaceAll("\n|\r", "");
        } catch (TransformerException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    /**
    * Convert a string to a xml document.
    */

    public static Document loadXmlFromString(String xml) {
        try {
            InputSource inputSource = new InputSource(new StringReader(xml));
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputSource);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            LOG.error(e.getMessage(), e);
            try {
                return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            } catch (ParserConfigurationException e1) {
                LOG.error(e1.getMessage(), e1);
                return null;
            }
        }
    }
}
