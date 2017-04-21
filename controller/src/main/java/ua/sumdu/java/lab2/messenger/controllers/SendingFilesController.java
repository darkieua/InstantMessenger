package ua.sumdu.java.lab2.messenger.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ua.sumdu.java.lab2.messenger.entities.GroupMapImpl;
import ua.sumdu.java.lab2.messenger.entities.SentFiles;
import ua.sumdu.java.lab2.messenger.entities.User;
import ua.sumdu.java.lab2.messenger.entities.UserMapImpl;
import ua.sumdu.java.lab2.messenger.handler.processing.RequestGeneratingImpl;
import ua.sumdu.java.lab2.messenger.listener.impl.ClientImpl;
import ua.sumdu.java.lab2.messenger.processing.GroupMapParserImpl;
import ua.sumdu.java.lab2.messenger.processing.UserMapParserImpl;

import java.io.File;
import java.util.List;

public class SendingFilesController {
  @FXML
  public RadioButton sendFilesToFriend;

  @FXML
  public ToggleGroup groupOrFriend;

  @FXML
  public RadioButton sendFilesToGroup;

  @FXML
  public Label choiceLabel;

  @FXML
  public ChoiceBox<String> listChoiceBox;

  @FXML
  public TableView<SentFiles.FileCharacteristics> fileTable;

  @FXML
  public TableColumn fileName;

  @FXML
  public TableColumn fileSize;

  @FXML
  public Button remote;

  SentFiles fileList = new SentFiles();

  @FXML
  public final void initialize() {
    selectFriend();
    fileName.setCellValueFactory(
        new PropertyValueFactory<>("name"));
    fileSize.setCellValueFactory(
        new PropertyValueFactory<>("shortSize"));
    fileTable.setItems(fileList.getObs());
    fileTable.getSelectionModel().selectedItemProperty().addListener(
        (observableValue, oldValue,
         newValue) -> remote.setVisible(true));
  }

  public void selectFiles(ActionEvent actionEvent) {
    Node source = (Node) actionEvent.getSource();
    Stage stage = (Stage) source.getScene().getWindow();
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Sending files");
    List<File> files = fileChooser.showOpenMultipleDialog(stage);
    String message = "";
    for (File file : files) {
      if (file.getName().endsWith(".exe")) {
        message += file.getName() + " (" + file.length() + ")\n";
      } else {
        fileList.addFile(file);
      }
    }
    fileTable.setItems(fileList.getObs());
    if (!"".equals(message)) {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Information");
      alert.setHeaderText("Successful");
      alert.setContentText("The following files can not be downloaded:\n" + message);
      alert.show();
    }
  }

  public void sentFiles(ActionEvent actionEvent) {
    Node source = (Node) actionEvent.getSource();
    Stage stage = (Stage) source.getScene().getWindow();
    if (sendFilesToFriend.isSelected()) {
      String userName = listChoiceBox.getSelectionModel().getSelectedItem();
      UserMapImpl friends = (UserMapImpl) UserMapParserImpl.getInstance().getFriends();
      for (User friend : friends.getMap().values()) {
        if (userName.equals(friend.getUsername())) {
          workWithClient(friend);
          stage.close();
          Alert alert = new Alert(Alert.AlertType.INFORMATION);
          alert.setTitle("Information");
          alert.setHeaderText("Successful");
          alert.setContentText("Request was successfully sent");
          alert.show();
          break;
        }
      }
    } else {
      String groupName = listChoiceBox.getSelectionModel().getSelectedItem();
      UserMapImpl group = (UserMapImpl) GroupMapParserImpl.getInstance().getUserMap(groupName);
      for (User visitor : group.getMap().values()) {
        if (!User.getCurrentUser().getUsername().equals(visitor.getUsername())) {
          workWithClient(visitor);
        }
      }
      stage.close();
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Information");
      alert.setHeaderText("Successful");
      alert.setContentText("Requests were successfully sent");
      alert.show();
    }
  }

  private void workWithClient(User user) {
    RequestGeneratingImpl requestGenerating = new RequestGeneratingImpl();
    String result = requestGenerating.dataRequest(fileList);
    new ClientImpl(user.getIpAddress(), user.getPort(), result).start();
  }

  public void remoteFile() {
    SentFiles.FileCharacteristics file = fileTable.getSelectionModel().getSelectedItem();
    fileList.getList().remove(file);
  }

  public void selectFriend() {
    choiceLabel.setText("Select friend: ");
    UserMapImpl users = (UserMapImpl) UserMapParserImpl.getInstance().getFriends();
    ObservableList<String> friendsName = FXCollections.observableArrayList();
    for (User user : users.getMap().values()) {
      friendsName.add(user.getUsername());
    }
    listChoiceBox.setItems(friendsName);
    listChoiceBox.getSelectionModel().selectFirst();
  }

  public void selectGroup() {
    choiceLabel.setText("Select group: ");
    GroupMapImpl groups = (GroupMapImpl) GroupMapParserImpl.getInstance().getGroupMap();
    ObservableList<String> friendsName = FXCollections.observableArrayList();
    for (String groupName : groups.getMap().keySet()) {
      friendsName.add(groupName);
    }
    listChoiceBox.setItems(friendsName);
    listChoiceBox.getSelectionModel().selectFirst();
  }
}
