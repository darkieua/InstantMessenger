package ua.sumdu.java.lab2.messenger.main;

import java.io.File;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.java.lab2.messenger.entities.User;

public class Main extends Application {
  private static final Logger LOG = LoggerFactory.getLogger(Main.class);

  @Override
  public void start(Stage primaryStage) {
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
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}
