package ua.sumdu.java.lab2.instant_messenger.parsers;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import ua.sumdu.java.lab2.instant_messenger.api.MessageMap;
import ua.sumdu.java.lab2.instant_messenger.api.MessageMapParser;
import ua.sumdu.java.lab2.instant_messenger.entities.Message;
import ua.sumdu.java.lab2.instant_messenger.entities.MessageMapImpl;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.*;
import java.io.*;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public enum XMLParser implements MessageMapParser {

    INSTANCE;

    private static final Logger LOG = LoggerFactory.getLogger(XMLParser.class);

    public Document getDocument(File file) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            return builder.parse(file);
        } catch (ParserConfigurationException | SAXException | IOException e) {

            return null;
        }
    }

    private void addMessage (Element messages, Message mess, Document doc) {
        Element mes = doc.createElement("message");
        messages.appendChild(mes);
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
        Map<String, byte[]> fileMap = mess.getFileMap();
        if (Objects.nonNull(fileMap)) {
            Element files = doc.createElement("files");
            mes.appendChild(files);
            for (Map.Entry<String, byte[]> f : fileMap.entrySet()) {
                Element fileObj = doc.createElement("file");
                files.appendChild(fileObj);
                Element fileName = doc.createElement("fileName");
                fileObj.appendChild(fileName);
                fileName.setTextContent(f.getKey());
                String data = new BigInteger(1, f.getValue()).toString(16);
                Element fileData = doc.createElement("fileData");
                fileObj.appendChild(fileData);
                fileData.setTextContent(data);
            }
        }
    }

    private Message parseMessage (Node node) {
        if (node.getNodeType() == node.ELEMENT_NODE) {
            Element elem = (Element) node;
            long countMilliSeconds = Long.parseLong(elem.getAttribute("date"));
            LocalDateTime date =
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(countMilliSeconds), ZoneId.systemDefault());
            String text = elem.getElementsByTagName("text").item(0).getTextContent();
            String senderUsername = elem.getElementsByTagName("senderUsername").item(0).getTextContent();
            String receiverUsername = elem.getElementsByTagName("receiverUsername").item(0).getTextContent();
            Message thisMessage = new Message(senderUsername, receiverUsername, text, date);
            if (Objects.nonNull(elem.getElementsByTagName("files").item(0))) {
                thisMessage.setFileMap(new TreeMap<>());
                Node files = elem.getElementsByTagName("files").item(0);
                NodeList fileNode = ((Element)files).getElementsByTagName("file");
                for (int j = 0; j < fileNode.getLength(); j++) {
                    Element currentFile = (Element) fileNode.item(j);
                    String fileName = currentFile.getElementsByTagName("fileName").item(0).getTextContent();
                    String fileData = currentFile.getElementsByTagName("fileData").item(0).getTextContent();
                    byte[] data = new BigInteger(fileData, 16).toByteArray();
                    thisMessage.getFileMap().put(fileName, data);
                }
            }
            return thisMessage;
        } else {
            return null;
        }
    }

    @Override
    public boolean write(MessageMap map, File file) {
        Document doc = getDocument(file);
        Element root;
        if (doc == null) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            try {
                builder = factory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                LOG.error(e.getMessage(), e);
                return false;
            }
            DOMImplementation impl = builder.getDOMImplementation();
            doc = impl.createDocument(null, null, null);
            root = doc.createElement("messages");
            doc.appendChild(root);
        } else {
            root = (Element)doc.getFirstChild();
        }
        MessageMapImpl newMap = (MessageMapImpl) map;
        for (Map.Entry<LocalDateTime, Message> messages : newMap.getMapForMails().entrySet()) {
            addMessage(root, messages.getValue(), doc);
        }
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(toXML(doc));
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException | TransformerException e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public MessageMap read(File file) {
        Document doc = getDocument(file);
        XPathFactory pathFactory = XPathFactory.newInstance();
        XPath xpath = pathFactory.newXPath();
        XPathExpression expr = null;
        MessageMapImpl map = null;
        try {
            expr = xpath.compile("messages/message[@date>"
                    + Date.from(LocalDateTime.now().minusMonths(1).atZone(ZoneId.systemDefault()).toInstant()).getTime()
                    + "]");
            NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            map = new MessageMapImpl();
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                map.addMessage(parseMessage(node));
            }
        } catch (XPathExpressionException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
        return map;
    }

    private String toXML(Document document) throws TransformerException, IOException {
        try {
            OutputFormat format = new OutputFormat(document);
            format.setLineWidth(65);
            format.setIndenting(true);
            format.setIndent(2);
            Writer out = new StringWriter();
            XMLSerializer serializer = new XMLSerializer(out, format);
            serializer.serialize(document);
            return out.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
