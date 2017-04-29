package ua.sumdu.java.lab2.messenger.controllers;

import static java.lang.Thread.sleep;
import static ua.sumdu.java.lab2.messenger.controllers.Constants.*;
import static ua.sumdu.java.lab2.messenger.controllers.Initialize.*;

import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import javafx.application.Platform;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.java.lab2.messenger.api.*;
import ua.sumdu.java.lab2.messenger.entities.*;
import ua.sumdu.java.lab2.messenger.handler.processing.*;
import ua.sumdu.java.lab2.messenger.listener.impl.ClientImpl;
import ua.sumdu.java.lab2.messenger.parsers.*;
import ua.sumdu.java.lab2.messenger.processing.*;

@SuppressWarnings("all")
public class MainController {
    private static final Logger LOG = LoggerFactory
            .getLogger(MainController.class);
    public AnchorPane anchorPane;
    public StackPane stackPane2;
    public javafx.scene.control.Label countMessages2;
    public StackPane stackPaneForDialog;
    @FXML
    private JFXButton btnOK;

    @FXML
    private VBox box;

    @FXML
    private javafx.scene.control.Label countMessages1;

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

    @FXML
    private StackPane stackPane1;
    @FXML
    private final JFXListView<String> blackList = new JFXListView<>();
    private final JFXListView<String> friendsList = new JFXListView<>();
    private final JFXListView<String> groupList = new JFXListView<>();
    private final JFXListView<String> messageList = new JFXListView<>();
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
    private JFXPopup blackListPopup;
    private JFXPopup groupsPopup;

    public Timer getTimer() {
        return timer;
    }

    private Timer timer;

    private boolean open = false;

    @FXML
    public final void initialize() {
        stackPaneForDialog.setVisible(false);
        stackPane1.setVisible(false);
        stackPane2.setVisible(false);
        ImageView image = new ImageView("ua/sumdu/java/lab2/messenger/images/clear.png");
        image.setFitWidth(20);
        image.setFitHeight(20);
        close.setGraphic(image);
        initHamburger();
        updateData();
        initFriendsPopups();
        initBlackListPopups();
        initGroupsPopups();
        messageList.setOnMouseClicked((event) -> {
            String name = messageList.getSelectionModel().getSelectedItem();
            MessageCounter messageCounter = MessageCounterParser.PARSER.getMessageCounter();
            messageCounter.remove(name);
            MessageCounterParser.PARSER.write(messageCounter);
            showMessages(name, tabPane, tabMap);
        });
        initMessageListPopup(messageList, newMessages, stackPane1);
        tabMap = new TreeMap<>();
        initFriends(friendsList);
        initGroups(groupList);
        initBlackList(blackList);
        textMessage.setVisible(false);
        btnOK.setVisible(false);
        close.setVisible(false);
        tabPane.getSelectionModel().selectedItemProperty()
                .addListener((observableValue, oldValue,
                              newValue) -> {
                    if (tabPane.getSelectionModel().getSelectedIndex() == 0) {
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

    private void initFriendsPopups() {
        VBox box1 = new VBox(friendsList);
        box1.setMinSize(100, 200);
        friendsPopup = new JFXPopup();
        friendsPopup.setPopupContent(box1);
        JFXButton deleteFriends = new JFXButton();
        buttonStyle(deleteFriends);
        deleteFriends.setText("Delete");
        deleteFriends.setOnMouseClicked((event) -> deleteFriend());
        stackPaneForDialog.getChildren().add(deleteFriends);
        JFXButton blockButton = new JFXButton();
        buttonStyle(blockButton);
        blockButton.setText("Block");
        blockButton.setOnMouseClicked((event) -> block());
        JFXButton btnShowFriendsChat = new JFXButton();
        buttonStyle(btnShowFriendsChat);
        btnShowFriendsChat.setText("Open");
        btnShowFriendsChat.setOnMouseClicked((event) -> {
            showMessages(friendsList.getSelectionModel()
                    .getSelectedItems()
                    .get(0), tabPane, tabMap);
        });
        VBox friendsOptions = new VBox(btnShowFriendsChat, deleteFriends, blockButton);
        JFXPopup friendsOption = new JFXPopup();
        friendsOption.setPopupContent(friendsOptions);
        friendsList.setOnMouseClicked((event) -> {
            if (Objects.nonNull(friendsList.getSelectionModel().getSelectedItem())) {
                friendsOption.show(friendsList, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, event.getX(), event.getY());
            }
        });
    }

    private void initBlackListPopups() {
        VBox box3 = new VBox(blackList);
        box3.setMinSize(100, 200);
        blackListPopup = new JFXPopup(box3);
        JFXButton delete = new JFXButton();
        buttonStyle(delete);
        delete.setText("Delete");
        delete.setOnMouseClicked((event) -> deleteFriend());
        stackPaneForDialog.getChildren().add(delete);
        JFXButton restore = new JFXButton();
        buttonStyle(restore);
        restore.setText("Restore");
        restore.setOnMouseClicked((event) -> restoreFromBlacklist());
        VBox blackListOptions = new VBox(restore, delete);
        JFXPopup blackListOption = new JFXPopup();
        blackListOption.setPopupContent(blackListOptions);
        blackList.setOnMouseClicked((event) -> {
            if (Objects.nonNull(blackList.getSelectionModel().getSelectedItem())) {
                blackListOption.show(blackList, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, event.getX(), event.getY());
            }
        });
    }

    private void initGroupsPopups() {
        JFXButton leave = new JFXButton();
        buttonStyle(leave);
        leave.setText("Leave");
        leave.setOnMouseClicked((event) -> leaveGroup(stackPaneForDialog, groupList, tabPane, tabMap));
        JFXButton info = new JFXButton();
        buttonStyle(info);
        info.setText("Get info");
        info.setOnMouseClicked((event) -> {
            Initialize init = new Initialize();
            init.groupInfo(groupList);
        });
        JFXButton btnShowGroupChat = new JFXButton();
        buttonStyle(btnShowGroupChat);
        btnShowGroupChat.setText("Open");
        btnShowGroupChat.setOnMouseClicked((event) -> showGroupChat());VBox box2 = new VBox(groupList);
        box2.setMinSize(100, 200);
        groupsPopup = new JFXPopup(box2);
        VBox groupOptions = new VBox(btnShowGroupChat, info, leave);
        JFXPopup groupOption = new JFXPopup();
        groupOption.setPopupContent(groupOptions);
        groupList.setOnMouseClicked((event) -> {
            if (Objects.nonNull(groupList.getSelectionModel().getSelectedItem())) {
                groupOption.show(groupList, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, event.getX(), event.getY());
            }

        });
    }
    
    private void initHamburger() {
        HamburgerBackArrowBasicTransition burgerTask = new HamburgerBackArrowBasicTransition(hamburger);
        burgerTask.setRate(-1);
        box.setVisible(true);
        drawer.setSidePane(box);
        hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED, (event)->{
            burgerTask.setRate(burgerTask.getRate()*-1);
            burgerTask.play();
            if (drawer.isShown()) {
                anchorPane.setPadding(new javafx.geometry.Insets(0, 0, 0, 0));
                drawer.setVisible(false);
                drawer.close();
                open = false;

            } else {
                drawer.setVisible(true);
                drawer.open();
                open = true;
                new Thread(() -> {
                    try {
                        sleep(500);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    anchorPane.setPadding(new javafx.geometry.Insets(0, 0, 0, 190));
                }).start();
            }
        });
    }
    
    private void createTimer() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    initFriends(friendsList);
                    initGroups(groupList);
                    initBlackList(blackList);
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
                    countMessages1.setText(String.valueOf(count));
                    countMessages2.setText(String.valueOf(count));
                    stackPane1.setVisible(count != 0);
                    stackPane2.setVisible(count != 0 && !open);
                });
            }
        };
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, ONE_SECOND);
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
                new ClientImpl(user.getIpAddress(), user.getPort(),
                        new MessageRequestGeneratingImpl().createRequestForNewMessage(message))
                        .start();
                return;
            }
        }
        UserMap groupMap = GroupMapParserImpl.getInstance()
                .getUserMap(receiver);
        for (User user : groupMap.getMap().values()) {
            new ClientImpl(user.getIpAddress(), user.getPort(),
                    new MessageRequestGeneratingImpl().createRequestForNewGroupMessage(message))
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
        Image icon = new Image("/ua/sumdu/java/lab2/messenger/images/"
                + "bubbles.png");
        stage.getIcons().add(icon);
        SendingFilesController sendingFilesController = sendingFilesFxmlLoader.getController();
        String name = tabPane.getSelectionModel().getSelectedItem().getText();
        sendingFilesController.setUsernameOrGroupname(name);
        sendingFilesController.initAfterSet();
        stage.setTitle("Sending files");
        stage.setScene(new Scene(root, SEND_FILES_WIDTH, SEND_FILES_HEIGHT));
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
        Image icon = new Image("/ua/sumdu/java/lab2/messenger/images/"
                + "bubbles.png");
        stage.getIcons().add(icon);
        stage.setTitle("Add to friends or group");
        stage.setScene(new Scene(root, ADD_WIDTH, ADD_HEIGHT));
        stage.setResizable(false);
        stage.showAndWait();
    }

    public final void newGroup() {
        stackPaneForDialog.setVisible(true);
        JFXDialogLayout content = new JFXDialogLayout();
        content.setStyle("-fx-background-color: #e0f7fa");
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().add(new Text("New group"));
        anchorPane.setStyle("-fx-background-color: #4dd0e1");
        content.setHeading(anchorPane);
        VBox box = new VBox();
        HBox hbox = new HBox();
        JFXTextField result = new JFXTextField();
        result.setUnFocusColor(javafx.scene.paint.Paint.valueOf("#009faf"));
        result.setFocusColor(javafx.scene.paint.Paint.valueOf("#88ffff"));
        hbox.getChildren().addAll(new Text("Please enter group name:"), result);
        box.getChildren().addAll(new Text("Create a new group"), hbox);
        content.setBody(box);
        JFXButton bOK = new JFXButton("Create");
        buttonStyle(bOK);
        JFXDialog dialog = new JFXDialog(stackPaneForDialog, content, JFXDialog.DialogTransition.CENTER);
        bOK.setOnAction((event) -> {
            GroupMap groups = GroupMapParserImpl.getInstance()
                    .getGroupMap();
            groups.addUser(result.getText(), User.getCurrentUser()
                    .setCategory(CategoryUsers.ADMIN));
            GroupMapParserImpl.getInstance()
                    .writeGroupMapToFile(GroupMapParserImpl.getInstance()
                            .groupMapToJSonString(groups));
            initGroups(groupList);
            dialog.close();
        });
        content.setActions(bOK);
        stackPaneForDialog.setVisible(true);
        dialog.show();
        dialog.setOnDialogClosed((event) -> {
            dialog.show();
            stackPaneForDialog.setVisible(false);
        });
    }
    
    public final void showGroupChat() {
        showMessages(groupList.getSelectionModel()
                .getSelectedItems()
                .get(0), tabPane, tabMap);
    }

    public final void block() {
        friendsPopup.hide();
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
        initFriends(friendsList);
        UserMapParserImpl.getInstance()
                .writeBlackListToFile(
                        UserMapParserImpl.getInstance()
                                .userMapToJSonString(blackListUsers));
        initBlackList(blackList);
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
        blackListPopup.hide();
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
        initFriends(friendsList);
        UserMapParserImpl.getInstance()
                .writeBlackListToFile(
                        UserMapParserImpl.getInstance()
                                .userMapToJSonString(blackListUsers));
        initBlackList(blackList);
    }

    public void deleteFriend() {
        if (friendsPopup.isShowing()) {
            friendsPopup.hide();
        } else {
            blackListPopup.hide();
        }
        stackPaneForDialog.setVisible(true);
        JFXDialogLayout content = new JFXDialogLayout();
        content.setStyle("-fx-background-color: #e0f7fa");
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().add(new Text("Removing from friends"));
        anchorPane.setStyle("-fx-background-color: #4dd0e1");
        content.setHeading(anchorPane);
        content.setBody(new Text("Are you sure?"));
        JFXButton bCancel = new JFXButton("No");
        JFXButton bOK = new JFXButton("Yes");
        buttonStyle(bOK);
        buttonStyle(bCancel);
        bOK.setOnAction((event) -> {
            stackPaneForDialog.setVisible(false);
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

    private void deleteUser(UserMap users, String username) {
        for (User user : users.getMap().values()) {
            if (username.equals(user.getUsername())) {
                new ClientImpl(user.getIpAddress(), user.getPort(), new RequestGeneratingImpl().creatingDeleteRequestFromFriends()).start();
                users.removeUser(user);
                return;
            }
        }
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

    public void closeTab() {
        tabPane.getTabs().remove(tabPane.getSelectionModel().getSelectedItem());
    }

    public void myInfo() {
        Stage stage = new Stage();
        FXMLLoader myInfoFxmlLoader = new FXMLLoader();
        myInfoFxmlLoader.setLocation(getClass()
                .getResource(
                        "/ua/sumdu/java/lab2/messenger/fxmls/"
                                + "myInfo.fxml"));
        Parent root = null;
        try {
            root = myInfoFxmlLoader.load();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        Image icon = new Image("/ua/sumdu/java/lab2/messenger/images/"
                + "bubbles.png");
        stage.getIcons().add(icon);
        stage.setTitle("MyInfo");
        stage.setScene(new Scene(root, SEND_FILES_WIDTH, SEND_FILES_HEIGHT));
        stage.show();
    }
}
