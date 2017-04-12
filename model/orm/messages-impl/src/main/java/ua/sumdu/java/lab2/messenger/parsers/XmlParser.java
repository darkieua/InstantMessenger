package ua.sumdu.java.lab2.messenger.parsers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder;
    try {
      builder = factory.newDocumentBuilder();
      return builder.parse(file);
    } catch (ParserConfigurationException | SAXException | IOException e) {
      return null;
    }
  }

  /**
   *Method adds new messages to the document element.
   */

  public void addMessage(Element messages, Message mess, Document doc) {
    Element mes = doc.createElement("message");
    if (Objects.isNull(messages)) {
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
    Map<String, byte[]> fileMap = mess.getFileMap();
    if (Objects.nonNull(fileMap)) {
      Element files = doc.createElement("files");
      mes.appendChild(files);
      for (Map.Entry<String, byte[]> currentfile : fileMap.entrySet()) {
        Element fileObj = doc.createElement("file");
        files.appendChild(fileObj);
        Element fileName = doc.createElement("fileName");
        fileObj.appendChild(fileName);
        fileName.setTextContent(currentfile.getKey());
        String data = new BigInteger(1, currentfile.getValue()).toString(16);
        Element fileData = doc.createElement("fileData");
        fileObj.appendChild(fileData);
        fileData.setTextContent(data);
      }
    }
  }

  /**
   * Method parses the messages of the current node.
   */

  public Message parseMessage(Node node) {
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
    if (Objects.isNull(doc)) {
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
    long date = Date.from(LocalDateTime.now().minusMonths(2).atZone(ZoneId.systemDefault())
        .toInstant()).getTime();
    return getMessagesFromSpecificDate(doc, date);
  }

  /**
   *Selecting from the document messages that were sent later than some time.
   */

  public MessageMap getMessagesFromSpecificDate(Document doc, long date) {
    XPathFactory pathFactory = XPathFactory.newInstance();
    XPath xpath = pathFactory.newXPath();
    XPathExpression expr;
    try {
      expr = xpath.compile("messages/message[@date>"
          + date + "]");
      if (Objects.nonNull(doc)) {
        NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        MessageMapImpl map = new MessageMapImpl();
        for (int i = 0; i < nodes.getLength(); i++) {
          Node node = nodes.item(i);
          map.addMessage(parseMessage(node));
        }
        return map;
      } else {
        return new MessageMapImpl();
      }
    } catch (XPathExpressionException e) {
      LOG.error(e.getMessage(), e);
      return new MessageMapImpl();
    }
  }

  /**
   * Converting a document to a xml string.
   */

  public String toXml(Document document) {
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
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  /**
  * Convert a string to a xml document.
  */

  public static Document loadXmlFromString(String xml) {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder;
    try {
      builder = factory.newDocumentBuilder();
      InputSource inputSource = new InputSource(new StringReader(xml));
      return builder.parse(inputSource);
    } catch (ParserConfigurationException | SAXException | IOException e) {
      LOG.error(e.getMessage(), e);
      return null;
    }

  }
}
