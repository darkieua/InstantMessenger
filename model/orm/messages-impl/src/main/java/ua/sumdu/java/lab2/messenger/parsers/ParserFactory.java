package ua.sumdu.java.lab2.messenger.parsers;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.java.lab2.messenger.api.MessageMapParser;

public final class ParserFactory {
  private static final Logger LOG = LoggerFactory.getLogger(ParserFactory.class);

  private static ParserFactory instance;

  private ParserFactory() {
  }

  /**
   * Returns a static instance of the class.
   * @return instance.
   */
  public static ParserFactory getInstance() {
    synchronized (ParserFactory.class) {
      LOG.debug("Create a new UserCreator");
      if (instance == null) {
        instance = new ParserFactory();
      }
      return instance;
    }
  }

  /**
   * The method analyzes the resulting file and, by its type, determines
   * the parser that is convenient for the inverting.
   * @param file file for parsing.
   * @return parser instance.
   */
  public MessageMapParser getParser(File file) {
    MessageMapParser parser = null;
    String str = getFileExtension(file);
    if ("xml".equals(str)) {
      parser = XmlParser.INSTANCE;
    }
    return parser;
  }

  private String getFileExtension(File file) {
    return file.getName();
  }
}
