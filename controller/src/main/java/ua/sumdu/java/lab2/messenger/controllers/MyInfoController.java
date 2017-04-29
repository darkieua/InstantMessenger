package ua.sumdu.java.lab2.messenger.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import ua.sumdu.java.lab2.messenger.api.Settings;
import ua.sumdu.java.lab2.messenger.entities.User;
import ua.sumdu.java.lab2.messenger.processing.SettingsParserImpl;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class MyInfoController {

    @FXML
    private Label userName;

    @FXML
    private Label email;

    @FXML
    private Label ipAddress;

    @FXML
    private Label port;

    @FXML
    private Label downloadPath;

    @FXML
    public final void initialize() {
        User currentUser = User.getCurrentUser();
        userName.setText(currentUser.getUsername());
        email.setText(currentUser.getEmail());
        ipAddress.setText(String.valueOf(currentUser.getIpAddress()).substring(1));
        port.setText(String.valueOf(currentUser.getPort()));
        downloadPath.setText(User.getDirectoryForDownloadFiles());
    }

    @FXML
    void changeDirectory(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(stage);
        if (Objects.nonNull(selectedDirectory.getPath())) {
            SettingsParserImpl settingsParser = new SettingsParserImpl();
            try {
                Settings settings = settingsParser.jsonToSettings(FileUtils.readFileToString(new File(User.getUserConfigPath()), "UTF-8"));
                settings.removeSetting("downloadPath");
                settings.putSetting("downloadPath", selectedDirectory.getPath());
                FileUtils.writeStringToFile(new File(User.getUserConfigPath()), settingsParser.settingsToJson(settings), "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            downloadPath.setText(selectedDirectory.getPath());
        }
    }

}
