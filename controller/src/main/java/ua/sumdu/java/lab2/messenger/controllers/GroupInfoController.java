package ua.sumdu.java.lab2.messenger.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ua.sumdu.java.lab2.messenger.api.GroupMap;
import ua.sumdu.java.lab2.messenger.api.UserMap;
import ua.sumdu.java.lab2.messenger.entities.CategoryUsers;
import ua.sumdu.java.lab2.messenger.entities.User;
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

    public void dataFilling(String groupName) {
        this.groupName.setText(groupName);
        UserMap users = GroupMapParserImpl.getInstance().getUserMap(groupName);
        userNames.setCellValueFactory(
                new PropertyValueFactory<>("username"));
        userCategory.setCellValueFactory(
                new PropertyValueFactory<>("category"));
        participants.setItems(users.getAllUsers());
        for (User user : users.getAllUsers()) {
            if (user.getCategory().name().equals(CategoryUsers.ADMIN.name())) {
                admin.setText(user.getUsername());
                break;
            }
        }
        remove.setVisible(false);
        block.setVisible(false);
        restore.setVisible(false);
        participants.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (admin.getText().equals(User.getCurrentUser().getUsername())) {
                remove.setVisible(true);
                block.setVisible(true);
                restore.setVisible(true);
            }
        });
    }

    public void removeUser(ActionEvent actionEvent) {
    }

    public void blockUser() {
        String username = participants.getSelectionModel().getSelectedItem().getUsername();
        GroupMap groupMap = GroupMapParserImpl.getInstance().getGroupMap();
        for (User user : groupMap.getMap().get(groupName.getText()).getMap().values()) {
            if (username.equals(user.getUsername())) {
                user.setCategory(CategoryUsers.BLACKLIST);
            }
        }
        GroupMapParserImpl.getInstance().writeGroupMapToFile(GroupMapParserImpl.getInstance().groupMapToJSonString(groupMap));
        participants.setItems(groupMap.getMap().get(groupName.getText()).getAllUsers());
    }

    public void restoreUser() {
        String username = participants.getSelectionModel().getSelectedItem().getUsername();
        GroupMap groupMap = GroupMapParserImpl.getInstance().getGroupMap();
        for (User user : groupMap.getMap().get(groupName.getText()).getMap().values()) {
            if (username.equals(user.getUsername())) {
                user.setCategory(CategoryUsers.VISITOR);
            }
        }
        GroupMapParserImpl.getInstance().writeGroupMapToFile(GroupMapParserImpl.getInstance().groupMapToJSonString(groupMap));
        participants.setItems(groupMap.getMap().get(groupName.getText()).getAllUsers());
    }
}
