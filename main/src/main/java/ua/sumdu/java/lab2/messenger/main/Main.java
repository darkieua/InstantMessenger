package ua.sumdu.java.lab2.messenger.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ua.sumdu.java.lab2.messenger.controllers.NewUserRegistrationController;
import ua.sumdu.java.lab2.messenger.entities.User;

import java.io.File;

public class Main extends Application {
  @Override
  public void start(Stage primaryStage) throws Exception {
    File userConfig = new File(User.getUserConfigPath());
    if (!userConfig.exists()) {
      FXMLLoader newUserRegistrationFxmlLoader = new FXMLLoader();
      newUserRegistrationFxmlLoader.setLocation(getClass().getResource("/ua/sumdu/java/lab2/messenger/fxmls/NewUserRegistration.fxml"));
      Parent root = newUserRegistrationFxmlLoader.load();
      primaryStage.setTitle("Registration");
      primaryStage.setScene(new Scene(root, 600, 300));
      primaryStage.setResizable(false);
      primaryStage.show();
    }
    //
   /* mainfxmlLoader.setLocation(getClass().getResource("/com/netcracker/java/yulia_shevchenko/lab1/view/main.fxml"));
    Parent root = mainfxmlLoader.load();
    primaryStage.setTitle("Task Manager");
    MainController mainController = mainfxmlLoader.getController();
    primaryStage.setScene(new Scene(root, this.width, this.height));
    primaryStage.setMinHeight(this.height);
    primaryStage.setMinWidth(this.width);
    primaryStage.show();
    primaryStage.setOnCloseRequest(closeEvent -> {
      mainController.getThread().close();
      LOGGER.info("end working");
    });*/
  }

  public static void main(String[] args) {
    launch(args);
  }
}
