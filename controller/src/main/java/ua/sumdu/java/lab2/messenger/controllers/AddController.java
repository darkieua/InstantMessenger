package ua.sumdu.java.lab2.messenger.controllers;

import static javafx.scene.control.Alert.AlertType.INFORMATION;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.java.lab2.messenger.api.UserMap;
import ua.sumdu.java.lab2.messenger.entities.CategoryUsers;
import ua.sumdu.java.lab2.messenger.entities.GroupMapImpl;
import ua.sumdu.java.lab2.messenger.entities.User;
import ua.sumdu.java.lab2.messenger.handler.processing.RequestGeneratingImpl;
import ua.sumdu.java.lab2.messenger.listener.impl.ClientImpl;
import ua.sumdu.java.lab2.messenger.processing.GroupMapParserImpl;

public class AddController {
    private static final Logger LOG = LoggerFactory
            .getLogger(AddController.class);
    public ToggleGroup addUser;

    @FXML
    private RadioButton addToFriends;

    @FXML
    private Label selectGroupLabel;

    @FXML
    private ComboBox<String> groupChoiceBox;

    @FXML
    private TextField ipAddress;

    @FXML
    private Label error;

    private static final String IP_REG_EXP
            = "^[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$";

    @FXML
    public final void initialize() {
        addToFriends.setSelected(true);
        showFriendsDetails();
    }

    public final void createRequest(final ActionEvent actionEvent) {
        String stringIP = ipAddress.getText();
        if (!validateIpAddress(stringIP)) {
            LOG.warn("Invalid IP-address");
            error.setText("Invalid IP-address\n");
            return;
        }
        error.setText("");
        try {
            workWithClient(InetAddress.getByName(stringIP));
        } catch (UnknownHostException e) {
            LOG.error(e.getMessage(), e);
        }
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source
                .getScene()
                .getWindow();
        stage.close();
        Platform.runLater(() -> {
            Alert alert = new Alert(INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("Successful");
            alert.setContentText("Your request was successfully sent.");
            alert.show();
        });
    }

    private void workWithClient(final InetAddress inetAddress) {
        RequestGeneratingImpl requestGenerating = new RequestGeneratingImpl();
        if (addToFriends.isSelected()) {
            new ClientImpl(inetAddress,
                    User.getCurrentUser()
                            .getPort(),
                    requestGenerating.creatingFriendsRequest())
                    .start();
        } else {
            new ClientImpl(inetAddress,
                    User.getCurrentUser()
                            .getPort(),
                    requestGenerating.createJoinRequestToGroup(groupChoiceBox
                            .getSelectionModel()
                            .getSelectedItem()))
                    .start();
        }
    }

    private boolean validateIpAddress(final String str) {
        return Pattern.compile(IP_REG_EXP)
                .matcher(str)
                .matches();
    }

    public final void showFriendsDetails() {
        groupChoiceBox.setVisible(false);
        selectGroupLabel.setVisible(false);
    }

    public final void showGroupsDetails() {
        groupChoiceBox.setVisible(true);
        selectGroupLabel.setVisible(true);
        ObservableList<String> list = FXCollections.observableArrayList();
        GroupMapImpl groups = (GroupMapImpl) GroupMapParserImpl.getInstance()
                .getGroupMap();
        for (String groupName : groups.getMap()
                .keySet()) {
            UserMap users = groups.getMap()
                    .get(groupName);
            for (User user : users.getMap().values()) {
                User currentUser = User.getCurrentUser()
                        .setCategory(CategoryUsers.ADMIN);
                if (user.equals(currentUser)) {
                    list.add(groupName);
                    break;
                }
            }
        }
        groupChoiceBox.setItems(list);
        groupChoiceBox.getSelectionModel()
                .selectFirst();
    }
}
