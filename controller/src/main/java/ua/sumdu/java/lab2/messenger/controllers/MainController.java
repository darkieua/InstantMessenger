package ua.sumdu.java.lab2.messenger.controllers;

import static javafx.scene.control.Alert.AlertType.CONFIRMATION;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.java.lab2.messenger.api.GroupMap;
import ua.sumdu.java.lab2.messenger.api.MessageMap;
import ua.sumdu.java.lab2.messenger.api.UserMap;
import ua.sumdu.java.lab2.messenger.entities.*;
import ua.sumdu.java.lab2.messenger.handler.processing.RequestGeneratingImpl;
import ua.sumdu.java.lab2.messenger.listener.impl.ClientImpl;
import ua.sumdu.java.lab2.messenger.parsers.MessageCounterParser;
import ua.sumdu.java.lab2.messenger.parsers.XmlParser;
import ua.sumdu.java.lab2.messenger.processing.GroupMapParserImpl;
import ua.sumdu.java.lab2.messenger.processing.UserMapParserImpl;

public class MainController {
    private static final Logger LOG = LoggerFactory
            .getLogger(MainController.class);
    @FXML
    private JFXButton btnOK;

    @FXML
    private VBox box;

    @FXML
    private Label countMessages;

    @FXML
    private JFXButton btnFriendsList;
    @FXML
    private JFXButton btnBlackList;
    @FXML
    private JFXButton btnGroups;
    @FXML
    private JFXButton close;
    @FXML
    private JFXButton newMessages;
    private JFXButton delete;
    private JFXButton leave;
    private JFXButton info;
    private JFXButton restore;
    private JFXButton blockButton;
    private JFXButton btnShowFriendsChat;
    private JFXButton btnShowGroupChat;

    @FXML
    private StackPane stackPane;
    @FXML
    private JFXListView<String> blackList;
    private JFXListView<String> friendsList;
    private JFXListView<String> groupList;
    private JFXListView<String> messageList;
    @FXML
    private JFXTabPane tabPane;

    @FXML
    private JFXTextField textMessage;

    private Map<String, ListView<Text>> tabMap;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private JFXPopup friendsPopup;
    private JFXPopup friendsOption;
    private JFXPopup blackListPopup;
    private JFXPopup blackListOption;
    private JFXPopup groupsPopup;
    private JFXPopup groupOption;
    private JFXPopup newMessagesPopup;

    public Timer getTimer() {
        return timer;
    }

    private Timer timer;
    private static final int ONE_SECOND = 1000;
    private static final int SEND_FILES_WIDTH = 500;
    private static final int SEND_FILES_HEIGHT = 350;
    private static final int ADD_WIDTH = 450;
    private static final int ADD_HEIGHT = 250;
    private static final int INFO_WIDTH = 450;
    private static final int INFO_HEIGHT = 400;

    @FXML
    public final void initialize() {
        stackPane.setVisible(false);
        ImageView image = new ImageView("ua/sumdu/java/lab2/messenger/images/clear.png");
        image.setFitWidth(20);
        image.setFitHeight(20);
        close.setGraphic(image);
        initHamburger();
        updateData();
        initPopups();
        User.getSystemMessageFile().delete();
        tabMap = new TreeMap<>();
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
        textMessage.setVisible(false);
        btnOK.setVisible(false);
        close.setVisible(false);
        tabPane.getSelectionModel().selectedItemProperty()
                .addListener((observableValue, oldValue,
                              newValue) -> {
                    if (tabPane.getSelectionModel().getSelectedItem().getText().equals("System")) {
                        textMessage.setVisible(false);
                        btnOK.setVisible(false);
                        close.setVisible(false);
                    } else {
                        textMessage.setVisible(true);
                        btnOK.setVisible(true);
                        close.setVisible(true);
                    }
        });
        createTimer();
    }

    private void initButtons() {
        leave = new JFXButton();
        leave.setText("leave group");
        leave.setOnMouseClicked((e) -> leaveGroup());
        info = new JFXButton();
        info.setText("Get info");
        info.setOnMouseClicked((e) -> groupInfo());
        delete = new JFXButton();
        delete.setText("Delete");
        delete.setOnMouseClicked((e) -> deleteFriend());
        restore = new JFXButton();
        restore.setText("Restore");
        restore.setOnMouseClicked((e) -> restoreFromBlacklist());
        blockButton = new JFXButton();
        blockButton.setText("Block");
        blockButton.setOnMouseClicked((e) -> block());
        btnShowFriendsChat = new JFXButton();
        btnShowFriendsChat.setText("Open");
        btnShowFriendsChat.setOnMouseClicked((e) -> showFriendsChat());
        btnShowGroupChat = new JFXButton();
        btnShowGroupChat.setText("Open");
        btnShowGroupChat.setOnMouseClicked((e) -> showGroupChat());
    }

    private void initPopups() {
        JFXListView<String> list1 = new JFXListView<>();
        friendsList = list1;
        VBox box1 = new VBox(list1);
        box1.setMinSize(100, 200);
        JFXListView<String> list2 = new JFXListView<>();
        groupList = list2;
        VBox box2 = new VBox(list2);
        box2.setMinSize(100, 200);
        JFXListView<String> list3 = new JFXListView<>();
        blackList = list3;
        VBox box3 = new VBox(list3);
        box3.setMinSize(100, 200);
        friendsPopup = new JFXPopup();
        friendsPopup.setPopupContent(box1);
        groupsPopup = new JFXPopup(box2);
        blackListPopup = new JFXPopup(box3);
        initButtons();
        VBox friendsOptions = new VBox(btnShowFriendsChat, blockButton, delete);
        friendsOption = new JFXPopup();
        friendsOption.setPopupContent(friendsOptions);
        friendsList.setOnMouseClicked((e) -> {
            if (Objects.nonNull(friendsList.getSelectionModel().getSelectedItem())) {
                friendsOption.show(friendsList, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, e.getX(), e.getY());
            }
        });
        VBox groupOptions = new VBox(btnShowGroupChat, info, leave);
        groupOption = new JFXPopup();
        groupOption.setPopupContent(groupOptions);
        groupList.setOnMouseClicked((e) -> {
            if (Objects.nonNull(groupList.getSelectionModel().getSelectedItem())) {
                groupOption.show(groupList, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, e.getX(), e.getY());
            }

        });
        VBox blackListOptions = new VBox(restore, delete);
        blackListOption = new JFXPopup();
        blackListOption.setPopupContent(blackListOptions);
        blackList.setOnMouseClicked((e) -> {
            if (Objects.nonNull(blackList.getSelectionModel().getSelectedItem())) {
                blackListOption.show(blackList, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, e.getX(), e.getY());
            }
        });
        messageList = new JFXListView<>();
        messageList.setOnMouseClicked((e) -> {
            String name = messageList.getSelectionModel().getSelectedItem();
            MessageCounter messageCounter = MessageCounterParser.PARSER.getMessageCounter();
            messageCounter.remove(name);
            MessageCounterParser.PARSER.write(messageCounter);
            showMessages(name);
        });
        VBox box4 = new VBox(messageList);
        box4.setMinSize(100, 200);
        newMessagesPopup = new JFXPopup(box4);
        newMessages.setOnMouseClicked((e) -> {
            if (stackPane.isVisible()) {
                newMessagesPopup.show(newMessages, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, e.getX(), e.getY());
            }
        });
    }

    private void initHamburger() {
        HamburgerBackArrowBasicTransition burgerTask = new HamburgerBackArrowBasicTransition(hamburger);
        burgerTask.setRate(-1);
        box.setVisible(true);
        drawer.setSidePane(box);
        hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED, (e)->{
            burgerTask.setRate(burgerTask.getRate()*-1);
            burgerTask.play();
            if (drawer.isShown()) {
                drawer.setVisible(false);
                drawer.close();
            } else {
                drawer.setVisible(true);
                drawer.open();
            }
        });
    }

    private void updateData() {
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
    }

    private void createTimer() {
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
                    MessageCounter messageCounter = MessageCounterParser.PARSER.getMessageCounter();
                    if (Objects.isNull(messageCounter)) {
                        messageCounter = new MessageCounter();
                    }
                    int count = 0;
                    messageList.setItems(messageCounter.getUserList());
                    for (int thisCount : messageCounter.getMap().values()) {
                        count += thisCount;
                    }
                    if (count != 0) {
                        countMessages.setText(String.valueOf(count));
                        stackPane.setVisible(true);
                    } else {
                        stackPane.setVisible(false);
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
            tabPane.getSelectionModel().select(newTab);
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

    public final void showFriendsChat() {
        showMessages(friendsList.getSelectionModel()
                .getSelectedItems()
                .get(0));
    }

    public final void showGroupChat() {
        showMessages(groupList.getSelectionModel()
                .getSelectedItems()
                .get(0));
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

    public void goToDownload() {
        Desktop desktop = null;
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
        }
        try {
            desktop.open(new File(User.getDirectoryForDownloadFiles()));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void showFriendsPopup(MouseEvent mouseEvent) {
        friendsPopup.show(btnFriendsList, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, mouseEvent.getX(), mouseEvent.getY());
    }

    public void showBlackListPopup(MouseEvent mouseEvent) {
        blackListPopup.show(btnBlackList, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, mouseEvent.getX(), mouseEvent.getY());
    }

    public void showGroupPopup(MouseEvent mouseEvent) {
        groupsPopup.show(btnGroups, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, mouseEvent.getX(), mouseEvent.getY());
    }

    public void closeTab(ActionEvent actionEvent) {
        tabPane.getTabs().remove(tabPane.getSelectionModel().getSelectedItem());
    }
}
