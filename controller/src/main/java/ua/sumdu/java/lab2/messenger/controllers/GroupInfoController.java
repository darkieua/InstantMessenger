package ua.sumdu.java.lab2.messenger.controllers;

import static ua.sumdu.java.lab2.messenger.entities.CategoryUsers.*;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import java.util.Objects;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import ua.sumdu.java.lab2.messenger.api.GroupMap;
import ua.sumdu.java.lab2.messenger.api.UserMap;
import ua.sumdu.java.lab2.messenger.entities.CategoryUsers;
import ua.sumdu.java.lab2.messenger.entities.User;
import ua.sumdu.java.lab2.messenger.handler.processing.RequestGeneratingImpl;
import ua.sumdu.java.lab2.messenger.listener.impl.ClientImpl;
import ua.sumdu.java.lab2.messenger.processing.GroupMapParserImpl;

public class GroupInfoController {

    public Label groupName;
    public Label admin;
    public Button remove;
    public Button block;
    public Button restore;
    public TableView<User> participants;
    public TableColumn userNames;
    public TableColumn userCategory;
    public StackPane stackPaneForDialog;

    public void dataFilling(String groupName) {
        this.groupName.setText(groupName);
        UserMap users = GroupMapParserImpl.getInstance().getUserMap(groupName);
        userNames.setCellValueFactory(
                new PropertyValueFactory<>("username"));
        userCategory.setCellValueFactory(
                new PropertyValueFactory<>("category"));
        participants.setItems(users.getAllUsers());
        for (User user : users.getAllUsers()) {
            if (user.getCategory().name().equals(ADMIN.name())) {
                admin.setText(user.getUsername());
                break;
            }
        }
        remove.setVisible(false);
        block.setVisible(false);
        restore.setVisible(false);
        participants.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            User user = participants.getSelectionModel().getSelectedItem();
            if (Objects.nonNull(user)) {
                if (admin.getText().equals(User.getCurrentUser().getUsername()) && !user.getUsername().equals(User.getCurrentUser().getUsername())) {
                    remove.setVisible(true);
                }
                if (BLACKLIST.name().equals(user.getCategory().name())) {
                    restore.setVisible(true);
                    return;
                } else if (!user.getCategory().name().equals(ADMIN.name())) {
                    block.setVisible(true);
                }
            }
        });
    }

    public void removeUser() {
        stackPaneForDialog.setVisible(true);
        JFXDialogLayout content = new JFXDialogLayout();
        content.setStyle("-fx-background-color: #e0f7fa");
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().add(new Text("Removing from group"));
        anchorPane.setStyle("-fx-background-color: #4dd0e1");
        content.setHeading(anchorPane);
        content.setBody(new Text("Are you sure?"));
        JFXButton bCancel = new JFXButton("No");
        JFXButton bOK = new JFXButton("Yes");
        Initialize.buttonStyle(bOK);
        Initialize.buttonStyle(bCancel);
        bOK.setOnAction((event) -> {
            User userForDelete = participants.getSelectionModel()
                    .getSelectedItem();
            new ClientImpl(userForDelete.getIpAddress(), userForDelete.getPort(),
                    new RequestGeneratingImpl()
                            .creatingDeleteRequestFromGroup(groupName.getText()))
                    .start();
            GroupMap groups = GroupMapParserImpl.getInstance().getGroupMap();
            groups.deleteUser(groupName.getText(), userForDelete);
            GroupMapParserImpl.getInstance()
                    .writeGroupMapToFile(
                            GroupMapParserImpl.getInstance()
                                    .groupMapToJSonString(groups));
            for (User user : groups.getMap().get(groupName.getText()).getMap().values()) {
                new ClientImpl(user.getIpAddress(), user.getPort(),
                        new RequestGeneratingImpl()
                                .updateGroupList(groupName.getText()))
                        .start();
            }
            remove.setVisible(false);
        });
        ButtonBar buttonBar = new ButtonBar();
        buttonBar.getButtons().addAll(bOK, bCancel);
        content.setActions(buttonBar);
        stackPaneForDialog.setVisible(true);
        JFXDialog dialog = new JFXDialog(stackPaneForDialog, content, JFXDialog.DialogTransition.CENTER);
        bCancel.setOnAction((event) -> {
            dialog.close();
            stackPaneForDialog.setVisible(false);
        });
        dialog.show();
        dialog.setOnDialogClosed((event) -> {
            dialog.show();
            stackPaneForDialog.setVisible(false);
        });
    }

    public void blockUser() {
        changeCategory(BLACKLIST);
        block.setVisible(false);
    }

    public void restoreUser() {
        changeCategory(VISITOR);
        restore.setVisible(false);
    }

    private void changeCategory(CategoryUsers categoryUsers) {
        String username = participants.getSelectionModel().getSelectedItem().getUsername();
        GroupMap groupMap = GroupMapParserImpl.getInstance().getGroupMap();
        for (User user : groupMap.getMap().get(groupName.getText()).getMap().values()) {
            if (username.equals(user.getUsername())) {
                user.setCategory(categoryUsers);
            }
        }
        GroupMapParserImpl.getInstance().writeGroupMapToFile(GroupMapParserImpl.getInstance().groupMapToJSonString(groupMap));
        participants.setItems(groupMap.getMap().get(groupName.getText()).getAllUsers());
    }
}
