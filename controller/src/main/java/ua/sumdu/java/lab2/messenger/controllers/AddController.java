package ua.sumdu.java.lab2.messenger.controllers;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
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
import ua.sumdu.java.lab2.messenger.entities.CategoryUsers;
import ua.sumdu.java.lab2.messenger.entities.GroupMapImpl;
import ua.sumdu.java.lab2.messenger.entities.User;
import ua.sumdu.java.lab2.messenger.entities.UserMapImpl;
import ua.sumdu.java.lab2.messenger.handler.processing.RequestGeneratingImpl;
import ua.sumdu.java.lab2.messenger.listener.impl.ClientImpl;
import ua.sumdu.java.lab2.messenger.processing.GroupMapParserImpl;

public class AddController {
  private static final Logger LOG = LoggerFactory.getLogger(AddController.class);

  @FXML
  public RadioButton addToFriends;

  @FXML
  public RadioButton addToGroup;

  @FXML
  public ToggleGroup addUser;

  @FXML
  public Label selectGroupLabel;

  @FXML
  public ChoiceBox<String> groupChoiceBox;

  @FXML
  public TextField ipAddress;

  @FXML
  public Label error;

  private static final String IP_REG_EXP = "^[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$";

  @FXML
  public final void initialize() {
    addToFriends.setSelected(true);
    showFriendsDetails();
  }

  public void createRequest(ActionEvent actionEvent) {
    String stringIP = ipAddress.getText();
    if (!validateIpAddress(stringIP)) {
      LOG.warn("Invalid IP-address");
      error.setText("Invalid IP-address\n");
      return;
    }
    error.setText("");
    try {
      InetAddress ipAddress = InetAddress.getByName(stringIP);
      workWithClient(ipAddress);
    } catch (UnknownHostException e) {
      LOG.error(e.getMessage(), e);
    }
    Node source = (Node) actionEvent.getSource();
    Stage stage = (Stage) source.getScene().getWindow();
    stage.close();
    Platform.runLater(() -> {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Information");
      alert.setHeaderText("Successful");
      alert.setContentText("Your request was successfully sent.");
      alert.show();
    });
  }

  private void workWithClient(InetAddress inetAddress) {
    RequestGeneratingImpl requestGenerating = new RequestGeneratingImpl();
    if (addToFriends.isSelected()) {
      ClientImpl client = new ClientImpl(inetAddress, User.getCurrentUser().getPort(), requestGenerating.addToFriends());
      client.start();
    } else {
      ClientImpl client = new ClientImpl(inetAddress, User.getCurrentUser().getPort(),
          requestGenerating.addToGroup(groupChoiceBox.getSelectionModel().getSelectedItem()));
      client.start();
    }
  }

  private boolean validateIpAddress(String str) {
    Pattern pattern = Pattern.compile(IP_REG_EXP);
    Matcher matcher = pattern.matcher(str);
    return matcher.matches();
  }

  public void showFriendsDetails() {
    groupChoiceBox.setVisible(false);
    selectGroupLabel.setVisible(false);
  }

  public void showGroupsDetails() {
    groupChoiceBox.setVisible(true);
    selectGroupLabel.setVisible(true);
    ObservableList<String> list = FXCollections.observableArrayList();
    GroupMapImpl groups = (GroupMapImpl) GroupMapParserImpl.getInstance().getGroupMap();
    for (String groupName : groups.getMap().keySet()) {
      UserMapImpl users = groups.getMap().get(groupName);
      for (User user : users.getMap().values()) {
        User currentUser = User.getCurrentUser().setCategory(CategoryUsers.ADMIN);
        if (user.equals(currentUser)) {
          list.add(groupName);
          break;
        }
      }
    }
    groupChoiceBox.setItems(list);
    groupChoiceBox.getSelectionModel().selectFirst();
  }
}
