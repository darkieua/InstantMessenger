package ua.sumdu.java.lab2.messenger.controllers;

import static javafx.scene.control.Alert.AlertType.CONFIRMATION;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXHamburger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.java.lab2.messenger.api.GroupMap;
import ua.sumdu.java.lab2.messenger.api.MessageMap;
import ua.sumdu.java.lab2.messenger.api.UserMap;
import ua.sumdu.java.lab2.messenger.entities.*;
import ua.sumdu.java.lab2.messenger.handler.processing.RequestGeneratingImpl;
import ua.sumdu.java.lab2.messenger.listener.impl.ClientImpl;
import ua.sumdu.java.lab2.messenger.parsers.XmlParser;
import ua.sumdu.java.lab2.messenger.processing.GroupMapParserImpl;
import ua.sumdu.java.lab2.messenger.processing.UserMapParserImpl;

public class MainController {
    private static final Logger LOG = LoggerFactory
            .getLogger(MainController.class);
    public JFXButton btnOK;
    public JFXHamburger hamburger;
    public JFXButton btnSentFiles;
    public JFXButton btnAdd;
    public JFXButton btnNewGroup;

    @FXML
    private Button delete;

    @FXML
    private Button leave;

    @FXML
    private Button info;

    @FXML
    private ListView<String> blackList;

    @FXML
    private Button restore;

    @FXML
    private Button blockButton;

    @FXML
    private ListView<String> friendsList;

    @FXML
    private ListView<String> groupList;

    @FXML
    private TabPane tabPane;

    @FXML
    private ListView<Text> systemMessages;

    @FXML
    private TextField textMessage;

    private Map<String, ListView<Text>> tabMap;

    public Timer getTimer() {
        return timer;
    }

    private Timer timer;
    private static final int ONE_SECOND = 1000;
    private static final int SEND_FILES_WIDTH = 500;
    private static final int SEND_FILES_HEIGHT = 300;
    private static final int ADD_WIDTH = 450;
    private static final int ADD_HEIGHT = 250;
    private static final int INFO_WIDTH = 450;
    private static final int INFO_HEIGHT = 400;

    @FXML
    public final void initialize() {
        UserMap friends = UserMapParserImpl.getInstance().getFriends();
        RequestGeneratingImpl requestGenerating = new RequestGeneratingImpl();
        long lastLogin = User.getLastLoginTime();
        for (User user : friends.getMap().values()) {
            new ClientImpl(user.getIpAddress(), user.getPort(),
                    requestGenerating
                            .createRequestForMessagesFromSpecificDate(lastLogin))
                    .start();
        }
        GroupMap groupMap = GroupMapParserImpl.getInstance().getGroupMap();
        for (String groupName : groupMap.getMap()
                .keySet()) {
            UserMap userMap = groupMap.getMap()
                    .get(groupName);
            for (User user : userMap.getMap()
                    .values()) {
                new ClientImpl(user.getIpAddress(), user.getPort(),
                        requestGenerating
                                .createRequestForGroupMessagesFromSpecificDate(lastLogin,
                                        groupName))
                        .start();
                new ClientImpl(user.getIpAddress(), user.getPort(),
                        requestGenerating
                                .createRequestForUpdateGroupList(groupName))
                        .start();
            }
        }
        User.getSystemMessageFile().delete();
        tabMap = new TreeMap<>();
        tabMap.put("system", systemMessages);
        Initialize.initFriends(friendsList);
        Initialize.initGroups(groupList);
        Initialize.initBlackList(blackList);
        blockButton.setVisible(false);
        restore.setVisible(false);
        delete.setVisible(false);
        leave.setVisible(false);
        info.setVisible(false);
        XmlParser.INSTANCE.write(new MessageMapImpl(), User.getSystemMessageFile());
        friendsList.getSelectionModel()
                .selectedItemProperty()
                .addListener((observableValue, oldValue,
                              newValue) -> {
                    blackList.getSelectionModel().clearSelection();
                    blockButton.setVisible(true);
                    delete.setVisible(true);
                });
        blackList.getSelectionModel()
                .selectedItemProperty()
                .addListener((observableValue, oldValue,
                              newValue) -> {
                    friendsList.getSelectionModel().clearSelection();
                    restore.setVisible(true);
                    delete.setVisible(true);
                });
        groupList.getSelectionModel()
                .selectedItemProperty()
                .addListener((observableValue, oldValue,
                              newValue) -> {
                    info.setVisible(true);
                    leave.setVisible(true);
                });
        Initialize.updateMessages(systemMessages, "system");
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    Initialize.initFriends(friendsList);
                    Initialize.initGroups(groupList);
                    Initialize.initBlackList(blackList);
                    for (String chatName : tabMap.keySet()) {
                        Initialize
                                .updateMessages(tabMap.get(chatName), chatName);
                    }
                });
            }
        };
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, ONE_SECOND);
    }

    private void showMessages(final String name) {
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
            newTab.setClosable(true);
            tabPane.getTabs().add(newTab);
            ListView<Text> chat;
            if (tabMap.get(name) == null) {
                chat = new ListView<>();
            } else {
                chat = tabMap.get(name);
            }
            tabMap.put(name, chat);
            newTab.setContent(chat);
            newTab.setClosable(true);
            //newTab.setOnCloseRequest(event -> tabMap.remove(name, chat));
            tabPane.getSelectionModel()
                    .select(newTab);
        }
        Initialize.updateMessages(tabMap.get(name), name);
    }

    public final void sentMessage() {
        String text = textMessage.getText();
        textMessage.clear();
        String receiver = tabPane.getSelectionModel()
                .getSelectedItem()
                .getText();
        Message message = new Message(User.getCurrentUser()
                .getUsername(),
                receiver, text,
                LocalDateTime.now());
        File file = new File(User.getUrlMessageDirectory()
                + "/" + receiver
                + ".xml");
        MessageMap messageMap = XmlParser.INSTANCE.read(file);
        messageMap.addMessage(message);
        XmlParser.INSTANCE.write(messageMap, file);
        UserMap friends = UserMapParserImpl.getInstance()
                .getFriends();
        for (User user : friends.getMap()
                .values()) {
            if (user.getUsername()
                    .equals(receiver)) {
                RequestGeneratingImpl requestGenerating
                        = new RequestGeneratingImpl();
                new ClientImpl(user.getIpAddress(), user.getPort(),
                        requestGenerating.createRequestForNewMessage(message))
                        .start();
                return;
            }
        }
        UserMap groupMap = GroupMapParserImpl.getInstance()
                .getUserMap(receiver);
        RequestGeneratingImpl requestGenerating
                = new RequestGeneratingImpl();
        for (User user : groupMap.getMap().values()) {
            new ClientImpl(user.getIpAddress(), user.getPort(),
                    requestGenerating.createRequestForNewGroupMessage(message))
                    .start();
        }
    }

    public final void sentFiles() {
        Stage stage = new Stage();
        FXMLLoader sendingFilesFxmlLoader = new FXMLLoader();
        sendingFilesFxmlLoader.setLocation(getClass()
                .getResource(
                        "/ua/sumdu/java/lab2/messenger/fxmls/"
                                + "SendingFiles.fxml"));
        Parent root = null;
        try {
            root = sendingFilesFxmlLoader.load();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        SendingFilesController sendingFilesController = sendingFilesFxmlLoader.getController();
        String name = tabPane.getSelectionModel().getSelectedItem().getText();
        sendingFilesController.setUsernameOrGroupname(name);
        sendingFilesController.initAfterSet();
        stage.setTitle("Add to friends or group");
        stage.setScene(new Scene(root, SEND_FILES_WIDTH, SEND_FILES_HEIGHT));
        stage.setResizable(false);
        stage.showAndWait();
    }

    public final void add() {
        Stage stage = new Stage();
        FXMLLoader addFxmlLoader = new FXMLLoader();
        addFxmlLoader.setLocation(getClass().getResource(
                "/ua/sumdu/java/lab2/messenger/fxmls/Add.fxml"));
        Parent root = null;
        try {
            root = addFxmlLoader.load();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        stage.setTitle("Add to friends or group");
        stage.setScene(new Scene(root, ADD_WIDTH, ADD_HEIGHT));
        stage.setResizable(false);
        stage.showAndWait();
        Initialize.updateMessages(systemMessages, "system");
    }

    public final void newGroup() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New group");
        dialog.setHeaderText("Create a new group");
        dialog.setContentText("Please enter group name:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            GroupMap groups = GroupMapParserImpl.getInstance()
                    .getGroupMap();
            groups.addUser(result.get(), User.getCurrentUser()
                            .setCategory(CategoryUsers.ADMIN));
            GroupMapParserImpl.getInstance()
                    .writeGroupMapToFile(GroupMapParserImpl.getInstance()
                                    .groupMapToJSonString(groups));
            Initialize.initGroups(groupList);
        }
    }

    public final void showFriendsChat(final MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            showMessages(friendsList.getSelectionModel()
                    .getSelectedItems()
                    .get(0));
        }
    }

    public final void showGroupChat(final MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            showMessages(groupList.getSelectionModel()
                    .getSelectedItems()
                    .get(0));
        }
    }

    public final void block() {
        String username = friendsList.getSelectionModel()
                .getSelectedItem();
        UserMap friends = UserMapParserImpl.getInstance()
                .getFriends();
        UserMap blackListUsers = UserMapParserImpl.getInstance()
                .getBlackList();
        for (User user : friends.getMap()
                .values()) {
            if (username.equals(user.getUsername())) {
                friends.removeUser(user);
                blackListUsers.addUser(user);
                break;
            }
        }
        UserMapParserImpl.getInstance()
                .writeUserMapToFile(
                        UserMapParserImpl.getInstance()
                                .userMapToJSonString(friends));
        Initialize.initFriends(friendsList);
        UserMapParserImpl.getInstance()
                .writeBlackListToFile(
                        UserMapParserImpl.getInstance()
                                .userMapToJSonString(blackListUsers));
        Initialize.initBlackList(blackList);
        for (Tab tab : tabPane.getTabs()) {
            if (tab.getText().equals(username)) {
                tabPane.getTabs().remove(tab);
                tabMap.remove(username, tabMap.get(username));
                break;
            }
        }
        tabPane.getSelectionModel()
                .select(0);
    }

    public final void restoreFromBlacklist() {
        String username = blackList.getSelectionModel()
                .getSelectedItem();
        UserMap blackListUsers = UserMapParserImpl.getInstance()
                .getBlackList();
        UserMap friends = UserMapParserImpl.getInstance()
                .getFriends();
        for (User user : blackListUsers.getMap()
                .values()) {
            if (username.equals(user.getUsername())) {
                blackListUsers.removeUser(user);
                friends.addUser(user);
                break;
            }
        }
        UserMapParserImpl.getInstance()
                .writeUserMapToFile(
                        UserMapParserImpl.getInstance()
                                .userMapToJSonString(friends));
        Initialize.initFriends(friendsList);
        UserMapParserImpl.getInstance()
                .writeBlackListToFile(
                        UserMapParserImpl.getInstance()
                                .userMapToJSonString(blackListUsers));
        Initialize.initBlackList(blackList);
    }

    public void deleteFriend() {
        Alert alert = new Alert(CONFIRMATION);
        alert.setTitle("Removing from friends");
        alert.setContentText("Are you sure?");
        Optional<ButtonType> result = alert.showAndWait();
        if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
            String username = friendsList.getSelectionModel()
                    .getSelectedItem();
            if (Objects.nonNull(username)) {
                UserMap friends = UserMapParserImpl.getInstance().getFriends();
                deleteUser(friends, username);
                UserMapParserImpl.getInstance().writeUserMapToFile(UserMapParserImpl.getInstance().userMapToJSonString(friends));
            } else {
                username = blackList.getSelectionModel().getSelectedItem();
                UserMap blackList = UserMapParserImpl.getInstance().getBlackList();
                deleteUser(blackList, username);
                UserMapParserImpl.getInstance().writeBlackListToFile(UserMapParserImpl.getInstance().userMapToJSonString(blackList));
            }
            delete.setVisible(false);
        }

    }

    private void deleteUser(UserMap users, String username) {
        for (User user : users.getMap().values()) {
            if (username.equals(user.getUsername())) {
                new ClientImpl(user.getIpAddress(), user.getPort(), new RequestGeneratingImpl().creatingDeleteRequestFromFriends()).start();
                users.removeUser(user);
                return;
            }
        }
    }

    public void leaveGroup() {
        Alert alert = new Alert(CONFIRMATION);
        alert.setTitle("Leave group");
        alert.setContentText("Are you sure?");
        Optional<ButtonType> result = alert.showAndWait();
        if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
            String groupName = groupList.getSelectionModel().getSelectedItem();
            GroupMap groupMap = GroupMapParserImpl.getInstance().getGroupMap();
            for (User user : groupMap.getMap().get(groupName).getMap().values()) {
                new ClientImpl(user.getIpAddress(), user.getPort(), new RequestGeneratingImpl().creatingDeleteRequestFromGroup(groupName)).start();
            }
            Map<String, UserMap> map = groupMap.getMap();
            map.remove(groupName);
            groupMap.setMap(map);
            GroupMapParserImpl.getInstance().writeGroupMapToFile(GroupMapParserImpl.getInstance().groupMapToJSonString(groupMap));
            Collection<Tab> tabs = tabPane.getTabs();
            for (Tab tab : tabs) {
                if (groupName.equals(tab.getText())) {
                    tabPane.getTabs().remove(tab);
                    break;
                }
            }
            tabPane.getSelectionModel().selectFirst();
            tabMap.remove(groupName);
        }
        Initialize.initGroups(groupList);
    }

    public void groupInfo() {
        Stage stage = new Stage();
        FXMLLoader groupInfoFxmlLoader = new FXMLLoader();
        groupInfoFxmlLoader.setLocation(getClass()
                .getResource(
                        "/ua/sumdu/java/lab2/messenger/fxmls/"
                                + "GroupInfo.fxml"));
        Parent root = null;
        try {
            root = groupInfoFxmlLoader.load();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        GroupInfoController groupInfoController = groupInfoFxmlLoader.getController();
        groupInfoController.dataFilling(groupList.getSelectionModel().getSelectedItem());
        stage.setTitle("Group info");
        stage.setScene(new Scene(root, INFO_WIDTH, INFO_HEIGHT));
        stage.setResizable(false);
        stage.show();
    }
}
