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
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
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
    private static final int REGISTRATION_WIDTH = 600;
    private static final int REGISTRATION_HEIGHT = 300;
    private static final int MAIN_WIDTH = 600;
    private static final int MAIN_HEIGHT = 400;
    private static final int ONE_SECOND = 100;

    @Override
    public final void start(final Stage primaryStage) {
        LOG.info("Start working");
        Stage stage = new Stage();
        Image icon = new Image("/ua/sumdu/java/lab2/messenger/images/"
                + "bubbles.png");
        File userConfig = new File(User.getUserConfigPath());
        if (!userConfig.exists()) {
            FXMLLoader newUserRegistrationFxmlLoader = new FXMLLoader();
            newUserRegistrationFxmlLoader.setLocation(getClass()
                    .getResource(
                            "/ua/sumdu/java/lab2/messenger/fxmls/"
                                    + "NewUserRegistration.fxml"));
            Parent root = null;
            try {
                root = newUserRegistrationFxmlLoader.load();
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
            stage.getIcons().add(icon);
            stage.setTitle("Registration");
            stage.setScene(new Scene(root, REGISTRATION_WIDTH,
                    REGISTRATION_HEIGHT));
            stage.setResizable(false);
            stage.showAndWait();
        }
        if (userConfig.exists()) {
            multiThreadedServer = new MultiThreadedServerImpl();
            multiThreadedServer.start();
            try {
                Thread.sleep(ONE_SECOND);
            } catch (InterruptedException e) {
                LOG.error(e.getMessage());
            }
            FXMLLoader mainFxmlLoader = new FXMLLoader();
            mainFxmlLoader.setLocation(getClass()
                    .getResource(
                            "/ua/sumdu/java/lab2/messenger/fxmls/Main.fxml"));
            Parent root = null;
            try {
                root = mainFxmlLoader.load();
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
            primaryStage.getIcons().add(icon);
            primaryStage.setTitle("Instant Messenger");
            primaryStage.setScene(new Scene(root, MAIN_WIDTH, MAIN_HEIGHT));
            primaryStage.setMinHeight(MAIN_HEIGHT);
            primaryStage.setMinWidth(MAIN_HEIGHT);
            primaryStage.show();
            MainController mainController = mainFxmlLoader.getController();
            primaryStage.setOnCloseRequest((WindowEvent closeEvent) -> {
                multiThreadedServer.stopServer();
                mainController.getTimer().cancel();
                SettingsImpl settings = new SettingsImpl();
                User thisUser = User.getCurrentUser();
                settings.putSetting("ipAddress",
                        String.valueOf(thisUser.getIpAddress())
                                .substring(1));
                settings.putSetting("port",
                        String.valueOf(thisUser.getPort()));
                settings.putSetting("downloadPath",
                        User.getDirectoryForDownloadFiles());
                settings.putSetting("username", thisUser.getUsername());
                settings.putSetting("email", thisUser.getEmail());
                Date out = Date.from(LocalDateTime.now()
                        .atZone(ZoneId.systemDefault())
                        .toInstant());
                settings.putSetting("lastLoginTime",
                        String.valueOf(out.getTime()));
                NewUserRegistrationController.writeToFile(userConfig,
                        new SettingsParserImpl()
                                .settingsToJson(settings));
                LOG.info("end working");
            });
        }
    }

    public static void main(final String[] args) {
        launch(args);
    }
}
