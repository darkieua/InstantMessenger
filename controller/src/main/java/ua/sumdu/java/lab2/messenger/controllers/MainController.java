package ua.sumdu.java.lab2.messenger.controllers;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.java.lab2.messenger.entities.*;
import ua.sumdu.java.lab2.messenger.handler.processing.RequestGeneratingImpl;
import ua.sumdu.java.lab2.messenger.listener.impl.ClientImpl;
import ua.sumdu.java.lab2.messenger.parsers.XmlParser;
import ua.sumdu.java.lab2.messenger.processing.GroupMapParserImpl;
import ua.sumdu.java.lab2.messenger.processing.UserMapParserImpl;

public class MainController {
  private static final Logger LOG = LoggerFactory.getLogger(MainController.class);

  @FXML
  public ListView<String> blackList;

  @FXML
  public Button restore;

  @FXML
  public Button blockButton;

  @FXML
  ListView<String> friendsList;

  @FXML
  ListView<String> groupList;

  @FXML
  TabPane tabPane;

  @FXML
  ListView<String> systemMessages;

  @FXML
  TextArea textMessage;

  private Map<String, ListView> tabMap;

  public Timer timer;

  @FXML
  public final void initialize() {
    UserMapImpl friends = (UserMapImpl) UserMapParserImpl.getInstance().getFriends();
    RequestGeneratingImpl requestGenerating = new RequestGeneratingImpl();
    long lastLogin = User.getLastLoginTime();
    for (User user : friends.getMap().values()) {
      new ClientImpl(user.getIpAddress(), user.getPort(), requestGenerating.messagesFromSpecificDate(lastLogin)).start();
    }
    GroupMapImpl groupMap = (GroupMapImpl) GroupMapParserImpl.getInstance().getGroupMap();
    for (String groupName : groupMap.getMap().keySet()) {
      UserMapImpl userMap = groupMap.getMap().get(groupName);
      for (User user : userMap.getMap().values()) {
        new ClientImpl(user.getIpAddress(), user.getPort(), requestGenerating.groupMessagesFromSpecificDate(lastLogin, groupName)).start();
        new ClientImpl(user.getIpAddress(), user.getPort(), requestGenerating.requestForUpdateGroupList(groupName)).start();
      }
    }
    tabMap = new TreeMap<>();
    tabMap.put("system", systemMessages);
    Initialize.initFriends(friendsList);
    Initialize.initGroups(groupList);
    Initialize.initBlackList(blackList);
    blockButton.setVisible(false);
    restore.setVisible(false);
    friendsList.getSelectionModel().selectedItemProperty().addListener(
        (observableValue, oldValue,
         newValue) -> blockButton.setVisible(true));
    blackList.getSelectionModel().selectedItemProperty().addListener(
        (observableValue, oldValue,
         newValue) -> restore.setVisible(true));
    Initialize.updateMessages(systemMessages, "system");
    TimerTask timerTask = new TimerTask() {
      @Override
      public void run() {
        Platform.runLater(() -> {
          Initialize.initFriends(friendsList);
          Initialize.initGroups(groupList);
          Initialize.initBlackList(blackList);
          for (String chatName : tabMap.keySet()) {
            Initialize.updateMessages(tabMap.get(chatName), chatName);
          }
        });
      }
    };
    timer = new Timer();
    timer.scheduleAtFixedRate(timerTask, 0, 900);
  }

  private void showMessages(String name) {
    boolean isFind = false;
    for (Tab currentTab : tabPane.getTabs().sorted()) {
      if (name.equals(currentTab.getText())) {
        tabPane.getSelectionModel().select(currentTab);
        isFind = true;
        break;
      }
    }
    if (!isFind) {
      Tab newTab = new Tab();
      newTab.setText(name);
      tabPane.getTabs().add(newTab);
      ListView<String> chat;
      if (tabMap.get(name) == null) {
        chat = new ListView<>();
      } else {
        chat = tabMap.get(name);
      }
      tabMap.put(name, chat);
      newTab.setContent(chat);
      newTab.setClosable(true);
      newTab.setOnCloseRequest(new EventHandler<Event>() {
        @Override
        public void handle(Event event) {
          tabMap.remove(name, chat);
        }
      });
      tabPane.getSelectionModel().select(newTab);
    }
    Initialize.updateMessages(tabMap.get(name), name);
  }

  public void sentMessage() {
    String text = textMessage.getText();
    textMessage.clear();
    String receiver = tabPane.getSelectionModel().getSelectedItem().getText();
    Message message = new Message(User.getCurrentUser().getUsername(), receiver, text, LocalDateTime.now());
    File file = new File(User.getUrlMessageDirectory() + "/" + receiver + ".xml");
    MessageMapImpl messageMap = (MessageMapImpl) XmlParser.INSTANCE.read(file);
    messageMap.addMessage(message);
    XmlParser.INSTANCE.write(messageMap, file);
    UserMapImpl friends = (UserMapImpl) UserMapParserImpl.getInstance().getFriends();
    for (User user : friends.getMap().values()) {
      if (user.getUsername().equals(receiver)) {
        RequestGeneratingImpl requestGenerating = new RequestGeneratingImpl();
        ClientImpl client = new ClientImpl(user.getIpAddress(), user.getPort(), requestGenerating.newMessage(message));
        client.start();
        return;
      }
    }
    UserMapImpl groupMap = (UserMapImpl) GroupMapParserImpl.getInstance().getUserMap(receiver);
    RequestGeneratingImpl requestGenerating = new RequestGeneratingImpl();
    for (User user : groupMap.getMap().values()) {
      ClientImpl client = new ClientImpl(user.getIpAddress(), user.getPort(), requestGenerating.newMessageToGroup(message));
      client.start();
    }
  }

  public void sentFiles() {
    Stage stage = new Stage();
    FXMLLoader sendingFilesController = new FXMLLoader();
    sendingFilesController.setLocation(getClass().getResource("/ua/sumdu/java/lab2/messenger/fxmls/SendingFiles.fxml"));
    Parent root = null;
    try {
      root = sendingFilesController.load();
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
    }
    stage.setTitle("Add to friends or group");
    stage.setScene(new Scene(root, 500, 350));
    stage.setResizable(false);
    stage.showAndWait();
  }

  public void add() {
    Stage stage = new Stage();
    FXMLLoader addController = new FXMLLoader();
    addController.setLocation(getClass().getResource("/ua/sumdu/java/lab2/messenger/fxmls/Add.fxml"));
    Parent root = null;
    try {
      root = addController.load();
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
    }
    stage.setTitle("Add to friends or group");
    stage.setScene(new Scene(root, 450, 250));
    stage.setResizable(false);
    stage.showAndWait();
    Initialize.updateMessages(systemMessages, "system");
  }

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
      Initialize.initGroups(groupList);
    }
  }

  public void showFriendsChat(MouseEvent mouseEvent) {
    if (mouseEvent.getClickCount() == 2) {
      showMessages(friendsList.getSelectionModel().getSelectedItems().get(0));
    }
  }

  public void showGroupChat(MouseEvent mouseEvent) {
    if (mouseEvent.getClickCount() == 2) {
      showMessages(groupList.getSelectionModel().getSelectedItems().get(0));
    }
  }

  public void block() {
    String username = friendsList.getSelectionModel().getSelectedItem();
    UserMapImpl friends = (UserMapImpl) UserMapParserImpl.getInstance().getFriends();
    UserMapImpl blackListUsers = (UserMapImpl) UserMapParserImpl.getInstance().getBlackList();
    for (User user : friends.getMap().values()) {
      if (username.equals(user.getUsername())) {
        friends.removeUser(user);
        blackListUsers.addUser(user);
        break;
      }
    }
    UserMapParserImpl.getInstance().writeUserMapToFile(UserMapParserImpl.getInstance().userMapToJSonString(friends));
    Initialize.initFriends(friendsList);
    UserMapParserImpl.getInstance().writeBlackListToFile(UserMapParserImpl.getInstance().userMapToJSonString(blackListUsers));
    Initialize.initBlackList(blackList);
    for (Tab tab : tabPane.getTabs()) {
      if (tab.getText().equals(username)) {
        tabPane.getTabs().remove(tab);
        tabMap.remove(username, tabMap.get(username));
        break;
      }
    }
    tabPane.getSelectionModel().select(0);
  }

  public void restoreFromBlacklist() {
    String username = blackList.getSelectionModel().getSelectedItem();
    UserMapImpl blackListUsers = (UserMapImpl) UserMapParserImpl.getInstance().getBlackList();
    UserMapImpl friends = (UserMapImpl) UserMapParserImpl.getInstance().getFriends();
    for (User user : blackListUsers.getMap().values()) {
      if (username.equals(user.getUsername())) {
        blackListUsers.removeUser(user);
        friends.addUser(user);
        break;
      }
    }
    UserMapParserImpl.getInstance().writeUserMapToFile(UserMapParserImpl.getInstance().userMapToJSonString(friends));
    Initialize.initFriends(friendsList);
    UserMapParserImpl.getInstance().writeBlackListToFile(UserMapParserImpl.getInstance().userMapToJSonString(blackListUsers));
    Initialize.initBlackList(blackList);
  }
}
