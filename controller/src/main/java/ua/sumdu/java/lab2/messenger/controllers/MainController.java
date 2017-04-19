package ua.sumdu.java.lab2.messenger.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ua.sumdu.java.lab2.messenger.entities.CategoryUsers;
import ua.sumdu.java.lab2.messenger.entities.GroupMapImpl;
import ua.sumdu.java.lab2.messenger.entities.User;
import ua.sumdu.java.lab2.messenger.processing.GroupMapParserImpl;

import java.util.Optional;
import java.util.Set;


public class MainController {

  @FXML
  ListView<String> friendsList;

  @FXML
  ListView groupList;

  @FXML
  TabPane tabPane;

  @FXML
  Tab systemChat;

  @FXML
  ListView systemMessages;

  @FXML
  TextArea textMessage;

  @FXML
  Button sentFiles;

/*  @FXML
  public final void initialize() {

  }*/

  private void initGroups() {
    GroupMapImpl groups = (GroupMapImpl) GroupMapParserImpl.getInstance().getGroupMap();
    Set<String> groupNames = groups.getMap().keySet();
    ObservableList<String> list = FXCollections.observableArrayList();
    list.addAll(groupNames);
    groupList.setItems(list);
  }

/*  private void initFriends() {

  }

  public void sentMessage(ActionEvent actionEvent) {
  }

  public void sentFiles(ActionEvent actionEvent) {
  }

  public void add(ActionEvent actionEvent) {
  }*/

  public void newGroup() {
    TextInputDialog dialog = new TextInputDialog();
    dialog.setTitle("New group");
    dialog.setHeaderText("Create a new group");
    dialog.setContentText("Please enter group name:");
    Optional<String> result = dialog.showAndWait();
    if (result.isPresent()){
      GroupMapImpl groups = (GroupMapImpl) GroupMapParserImpl.getInstance().getGroupMap();
      groups.addUser(result.get(), User.getCurrentUser().setCategory(CategoryUsers.ADMIN));
      GroupMapParserImpl.getInstance().writeGroupMapToFile(GroupMapParserImpl.getInstance().groupMapToJSonString(groups));
      initGroups();
    }
  }
}
