package ua.sumdu.java.lab2.messenger.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.java.lab2.messenger.entities.User;
import ua.sumdu.java.lab2.messenger.processing.SettingsImpl;
import ua.sumdu.java.lab2.messenger.processing.SettingsParserImpl;
import ua.sumdu.java.lab2.messenger.processing.UserCreatorImpl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class NewUserRegistrationController {
  private static final Logger LOG = LoggerFactory.getLogger(NewUserRegistrationController.class);

  @FXML
  public TextField username;

  @FXML
  public TextField email;

  @FXML
  public Label directoryPath;

  @FXML
  public Label error;

  private String path = "";

  public void registration(ActionEvent actionEvent) {
    Node source = (Node) actionEvent.getSource();
    Stage stage = (Stage) source.getScene().getWindow();
    String errorMessage = "";
    if (!UserCreatorImpl.INSTANCE.validateUsername(username.getText())) {
      LOG.warn("Invalid username");
      errorMessage += "Invalid username\n";
    }
    if (!UserCreatorImpl.INSTANCE.validateEmail(email.getText())) {
      LOG.warn("Invalid e-mail");
      errorMessage += "Invalid e-mail\n";
    }
    if ("".equals(path)) {
      LOG.warn("Folder for saving files is not selected");
      errorMessage += "Folder for saving files is not selected\n";
    }
    if ("".equals(errorMessage)) {
      error.setText("");
      SettingsImpl settings = new SettingsImpl();
      try {
        String ipAddress = InetAddress.getLocalHost().getHostAddress();
        settings.putSetting("ipAddress", ipAddress);
      } catch (UnknownHostException e) {
        LOG.error(e.getMessage(), e);
        settings.putSetting("ipAddress", "");
      }
      settings.putSetting("port", String.valueOf(9696));
      settings.putSetting("downloadPath", path);
      settings.putSetting("username", username.getText());
      settings.putSetting("email", email.getText());
      Date out = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
      settings.putSetting("lastLoginTime", String.valueOf(out.getTime()));
      SettingsParserImpl settingsParser = new SettingsParserImpl();
      String result = settingsParser.settingsToJson(settings);
      File userConfig = new File(User.getUserConfigPath());
      try {
        userConfig.createNewFile();
      } catch (IOException e) {
        LOG.error(e.getMessage(), e);
      }
      writeToFile(userConfig, result);
      stage.close();
    } else {
      error.setText(errorMessage);
    }
  }

  public static void writeToFile(File file , String str) {
    try (FileWriter writer = new FileWriter(file, false)) {
      writer.write(str);
      writer.flush();
      writer.close();
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  public void directorySelection(ActionEvent actionEvent) {
    Node source = (Node) actionEvent.getSource();
    Stage stage = (Stage) source.getScene().getWindow();
    DirectoryChooser directoryChooser = new DirectoryChooser();
    File selectedDirectory = directoryChooser.showDialog(stage);
    directoryPath.setText(selectedDirectory.getPath());
    path = selectedDirectory.getPath();
  }
}
