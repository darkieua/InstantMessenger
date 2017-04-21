package ua.sumdu.java.lab2.messenger.main;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.java.lab2.messenger.controllers.MainController;
import ua.sumdu.java.lab2.messenger.controllers.NewUserRegistrationController;
import ua.sumdu.java.lab2.messenger.entities.User;
import ua.sumdu.java.lab2.messenger.listener.impl.MultiThreadedServerImpl;
import ua.sumdu.java.lab2.messenger.processing.SettingsImpl;
import ua.sumdu.java.lab2.messenger.processing.SettingsParserImpl;

public class Main extends Application {
  private static final Logger LOG = LoggerFactory.getLogger(Main.class);

  private MultiThreadedServerImpl multiThreadedServer;

  @Override
  public void start(Stage primaryStage) {
    LOG.info("Start working");
    Stage stage = new Stage();
    File userConfig = new File(User.getUserConfigPath());
    if (!userConfig.exists()) {
      FXMLLoader newUserRegistrationFxmlLoader = new FXMLLoader();
      newUserRegistrationFxmlLoader.setLocation(getClass().getResource("/ua/sumdu/java/lab2/messenger/fxmls/NewUserRegistration.fxml"));
      Parent root = null;
      try {
        root = newUserRegistrationFxmlLoader.load();
      } catch (IOException e) {
        LOG.error(e.getMessage(), e);
      }
      stage.setTitle("Registration");
      stage.setScene(new Scene(root, 600, 300));
      stage.setResizable(false);
      stage.showAndWait();
    }
    if (userConfig.exists()) {
      multiThreadedServer = new MultiThreadedServerImpl();
      multiThreadedServer.start();
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        LOG.error(e.getMessage());
      }
      FXMLLoader mainFxmlLoader = new FXMLLoader();
      mainFxmlLoader.setLocation(getClass().getResource("/ua/sumdu/java/lab2/messenger/fxmls/Main.fxml"));
      Parent root = null;
      try {
        root = mainFxmlLoader.load();
      } catch (IOException e) {
        LOG.error(e.getMessage(), e);
      }
      primaryStage.setTitle("Instant Messenger");
      primaryStage.setScene(new Scene(root, 600, 400));
      primaryStage.setMinHeight(400);
      primaryStage.setMinWidth(600);
      primaryStage.show();
      MainController mainController = mainFxmlLoader.getController();
      primaryStage.setOnCloseRequest(closeEvent -> {
        multiThreadedServer.stopServer();
        mainController.timer.cancel();
        SettingsImpl settings = new SettingsImpl();
        User thisUser = User.getCurrentUser();
        settings.putSetting("ipAddress", String.valueOf(thisUser.getIpAddress()).substring(1));
        settings.putSetting("port", String.valueOf(thisUser.getPort()));
        settings.putSetting("downloadPath", User.getDirectoryForDownloadFiles());
        settings.putSetting("username", thisUser.getUsername());
        settings.putSetting("email", thisUser.getEmail());
        Date out = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        settings.putSetting("lastLoginTime", String.valueOf(out.getTime()));
        SettingsParserImpl settingsParser = new SettingsParserImpl();
        String result = settingsParser.settingsToJson(settings);
        NewUserRegistrationController.writeToFile(userConfig, result);
        LOG.info("end working");
      });
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}
