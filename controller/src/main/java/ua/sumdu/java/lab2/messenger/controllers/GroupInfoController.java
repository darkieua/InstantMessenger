package ua.sumdu.java.lab2.messenger.controllers;

import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static ua.sumdu.java.lab2.messenger.entities.CategoryUsers.*;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ua.sumdu.java.lab2.messenger.api.GroupMap;
import ua.sumdu.java.lab2.messenger.api.UserMap;
import ua.sumdu.java.lab2.messenger.entities.CategoryUsers;
import ua.sumdu.java.lab2.messenger.entities.GroupMapImpl;
import ua.sumdu.java.lab2.messenger.entities.User;
import ua.sumdu.java.lab2.messenger.handler.processing.RequestGeneratingImpl;
import ua.sumdu.java.lab2.messenger.listener.impl.ClientImpl;
import ua.sumdu.java.lab2.messenger.processing.GroupMapParserImpl;
import ua.sumdu.java.lab2.messenger.processing.UserMapParserImpl;

import java.util.Objects;
import java.util.Optional;

public class GroupInfoController {

    public Label groupName;
    public Label admin;
    public Button remove;
    public Button block;
    public Button restore;
    public TableView<User> participants;
    public TableColumn userNames;
    public TableColumn userCategory;

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
            if (admin.getText().equals(User.getCurrentUser().getUsername()) && !user.getUsername().equals(User.getCurrentUser().getUsername())) {
                remove.setVisible(true);
            }
            if (Objects.nonNull(user)) {
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
        Alert alert = new Alert(CONFIRMATION);
        alert.setTitle("Removing from group");
        alert.setContentText("Are you sure?");
        Optional<ButtonType> result = alert.showAndWait();
        if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
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
        }
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
