package ua.sumdu.java.lab2.messenger.controllers;

import java.io.File;
import java.util.List;
import java.util.Objects;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXRadioButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import ua.sumdu.java.lab2.messenger.api.GroupMap;
import ua.sumdu.java.lab2.messenger.api.UserMap;
import ua.sumdu.java.lab2.messenger.entities.SentFiles;
import ua.sumdu.java.lab2.messenger.entities.User;
import ua.sumdu.java.lab2.messenger.handler.processing.RequestGeneratingImpl;
import ua.sumdu.java.lab2.messenger.listener.impl.ClientImpl;
import ua.sumdu.java.lab2.messenger.processing.GroupMapParserImpl;
import ua.sumdu.java.lab2.messenger.processing.UserMapParserImpl;

public class SendingFilesController {
    @FXML
    private JFXRadioButton sendFilesToGroup;
    @FXML
    private JFXRadioButton sendFilesToFriend;

    @FXML
    private Label choiceLabel;

    @FXML
    private JFXComboBox<String> listChoiceBox;

    @FXML
    private TableView<SentFiles.FileCharacteristics> fileTable;

    @FXML
    private TableColumn fileName;

    @FXML
    private TableColumn fileSize;

    @FXML
    private JFXButton remote;

    private final SentFiles fileList = new SentFiles();

    public void setUsernameOrGroupname(String usernameOrGroupname) {
        this.usernameOrGroupname = usernameOrGroupname;
    }

    private String usernameOrGroupname;

    private boolean set = false;

    @FXML
    public final void initAfterSet() {
        UserMap friends = UserMapParserImpl.getInstance().getFriends();
        for (User user : friends.getAllUsers()) {
            if (usernameOrGroupname.equals(user.getUsername())) {
                set = true;
                sendFilesToFriend.setSelected(true);
                selectFriend();

            }
        }
        if (!set) {
            sendFilesToGroup.setSelected(true);
            selectGroup();
        }
        listChoiceBox.getSelectionModel().select(usernameOrGroupname);
        fileName.setCellValueFactory(
                new PropertyValueFactory<>("name"));
        fileSize.setCellValueFactory(
                new PropertyValueFactory<>("shortSize"));
        fileTable.setItems(fileList.getObs());
        fileTable.getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (observableValue, oldValue, newValue) ->
                                remote.setVisible(true));
    }

    public final void selectFiles(final ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource())
                .getScene()
                .getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sending files");
        List<File> files = fileChooser.showOpenMultipleDialog(stage);
        StringBuilder message = new StringBuilder();
        if (Objects.nonNull(files)) {
            for (File file : files) {
                if (file.getName().endsWith(".exe")) {
                    message.append(file.getName())
                            .append(" (")
                            .append(file.length())
                            .append(")\n");
                } else {
                    fileList.addFile(file);
                }
            }
            fileList.updateObs();
            fileTable.setItems(fileList.getObs());
            if (!"".equals(message.toString())) {
                Notifications notification = Notifications.create()
                        .title("Information")
                        .darkStyle()
                        .graphic(null)
                        .text("The following files can not be downloaded:\n"
                                + message.toString())
                        .hideAfter(Duration.seconds(5))
                        .position(Pos.BOTTOM_RIGHT);
                notification.showConfirm();
            }
        }
    }

    public final void sentFiles(final ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource())
                .getScene()
                .getWindow();
        if (sendFilesToFriend.isSelected()) {
            String userName = listChoiceBox.getSelectionModel()
                    .getSelectedItem();
            UserMap friends = UserMapParserImpl.getInstance()
                    .getFriends();
            for (User friend : friends.getMap()
                    .values()) {
                if (userName.equals(friend.getUsername())) {
                    workWithClient(friend);
                    stage.close();
                    Notifications notification = Notifications.create()
                            .title("Information")
                            .darkStyle()
                            .graphic(null)
                            .text("Request was successfully sent")
                            .hideAfter(Duration.seconds(5))
                            .position(Pos.BOTTOM_RIGHT);
                    notification.showConfirm();
                    break;
                }
            }
        } else {
            String groupName = listChoiceBox.getSelectionModel()
                    .getSelectedItem();
            UserMap group = GroupMapParserImpl.getInstance()
                    .getUserMap(groupName);
            for (User visitor : group.getMap()
                    .values()) {
                if (!User.getCurrentUser()
                        .getUsername()
                        .equals(visitor.getUsername())) {
                    workWithClient(visitor);
                }
            }
            stage.close();
            Notifications notification = Notifications.create()
                    .title("Information")
                    .darkStyle()
                    .graphic(null)
                    .text("Request was successfully sent")
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.BOTTOM_RIGHT);
            notification.showConfirm();
        }
    }

    private void workWithClient(final User user) {
        new ClientImpl(user.getIpAddress(), user.getPort(),
                new RequestGeneratingImpl()
                        .createDataRequest(fileList))
                .start();
    }

    public final void remoteFile() {
        SentFiles.FileCharacteristics file = fileTable.getSelectionModel()
                .getSelectedItem();
        fileList.getList()
                .remove(file);
        fileList.updateObs();
        fileTable.setItems(fileList.getObs());
    }

    public final void selectFriend() {
        choiceLabel.setText("Select friend: ");
        UserMap users = UserMapParserImpl.getInstance()
                .getFriends();
        ObservableList<String> friendsName = FXCollections
                .observableArrayList();
        for (User user : users.getMap()
                .values()) {
            friendsName.add(user.getUsername());
        }
        listChoiceBox.setItems(friendsName);
        listChoiceBox.getSelectionModel().select(usernameOrGroupname);
    }

    public final void selectGroup() {
        choiceLabel.setText("Select group: ");
        GroupMap groups = GroupMapParserImpl.getInstance()
                .getGroupMap();
        ObservableList<String> friendsName = FXCollections
                .observableArrayList();
        friendsName.addAll(groups.getMap()
                .keySet());
        listChoiceBox.setItems(friendsName);
        listChoiceBox.getSelectionModel().select(usernameOrGroupname);
    }
}
