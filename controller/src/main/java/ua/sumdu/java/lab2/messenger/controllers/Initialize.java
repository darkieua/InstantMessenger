package ua.sumdu.java.lab2.messenger.controllers;

import static ua.sumdu.java.lab2.messenger.controllers.Constants.*;
import static ua.sumdu.java.lab2.messenger.entities.CategoryUsers.*;

import com.jfoenix.controls.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javafx.collections.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ua.sumdu.java.lab2.messenger.api.*;
import ua.sumdu.java.lab2.messenger.entities.Message;
import ua.sumdu.java.lab2.messenger.entities.User;
import ua.sumdu.java.lab2.messenger.handler.processing.MessageRequestGeneratingImpl;
import ua.sumdu.java.lab2.messenger.handler.processing.RequestGeneratingImpl;
import ua.sumdu.java.lab2.messenger.listener.impl.ClientImpl;
import ua.sumdu.java.lab2.messenger.parsers.XmlParser;
import ua.sumdu.java.lab2.messenger.processing.*;

public final class Initialize {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Initialize.class);

    public Initialize() {
        LOG.debug("Initialize constructor");
    }

    static void updateData() {
        UserMap friends = UserMapParserImpl.getInstance().getFriends();
        long lastLogin = User.getLastLoginTime();
        for (User user : friends.getMap().values()) {
            new ClientImpl(user.getIpAddress(), user.getPort(),
                    new MessageRequestGeneratingImpl()
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
                        new MessageRequestGeneratingImpl()
                                .createRequestForGroupMessagesFromSpecificDate(lastLogin,
                                        groupName))
                        .start();
                new ClientImpl(user.getIpAddress(), user.getPort(),
                        new RequestGeneratingImpl()
                                .createRequestForUpdateGroupList(groupName))
                        .start();
            }
        }
    }

    static void initMessageListPopup(JFXListView<String> messageList, JFXButton newMessages, StackPane stackPane1) {
        VBox box4 = new VBox(messageList);
        box4.setMinSize(100, 200);
        JFXPopup newMessagesPopup = new JFXPopup(box4);
        newMessages.setOnMouseClicked((event) -> {
            if (stackPane1.isVisible()) {
                newMessagesPopup.show(newMessages, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, event.getX(), event.getY());
            }
        });
    }

    static void buttonStyle(JFXButton btn) {
        btn.setMinSize(70, 30);
        btn.setStyle("-fx-background-color: #6ff9ff");
        btn.setButtonType(JFXButton.ButtonType.RAISED);
        btn.setRipplerFill(javafx.scene.paint.Paint.valueOf("#3709eb"));
    }

    static void showMessages(final String name, TabPane tabPane, Map<String, ListView<Text>> tabMap) {
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
        updateMessages(tabMap.get(name), name);
    }

    public static void initFriends(final JFXListView<String> friendsList) {
        UserMap friends = UserMapParserImpl.getInstance()
                .getFriends();
        ObservableList<String> list = FXCollections.observableArrayList();
        for (User user : friends.getMap()
                .values()) {
            if (!user.getCategory().name()
                    .equals(EMPTY_USER.name())
                    && !user.getCategory().name()
                    .equals(CURRENT_USER.name())) {
                list.add(user.getUsername());
            }
        }
        friendsList.setItems(list);
    }

    public static void initGroups(final JFXListView<String> groupList) {
        GroupMap groups = GroupMapParserImpl.getInstance()
                .getGroupMap();
        Set<String> groupNames = groups.getMap()
                .keySet();
        ObservableList<String> list = FXCollections.observableArrayList();
        for (String groupName : groupNames) {
            if (groups.getMap()
                    .get(groupName)
                    .getMap()
                    .size() != 0) {
                list.add(groupName);
            }
        }
        groupList.setItems(list);
    }

    public static void initBlackList(final JFXListView<String> blackList) {
        UserMap blackListUsers = UserMapParserImpl
                .getInstance()
                .getBlackList();
        ObservableList<String> list = FXCollections.observableArrayList();
        for (User user : blackListUsers.getMap()
                .values()) {
            list.add(user.getUsername());
        }
        blackList.setItems(list);
    }

    public static void updateMessages(final ListView<Text> chat,
                                      final String groupName) {
        File messages = new File(User.getUrlMessageDirectory()
                + File.separator
                + groupName
                + ".xml");
        ObservableList<Text> list = FXCollections.observableArrayList();
        MessageMap messageMap = XmlParser.INSTANCE.read(messages);
        for (Message message : messageMap.getMapForMails().values()) {
            Text text = new Text(message.getSender() + ": "
                    + message.getText() + " ("
                    + message.getTimeSending() + ")");
            text.wrappingWidthProperty().bind(chat.widthProperty());
            list.add(text);
        }
        chat.setItems(list);
    }

    static void leaveGroup(StackPane stackPaneForDialog, JFXListView<String> groupList, TabPane tabPane, Map<String, ListView<Text>> tabMap) {
        stackPaneForDialog.setVisible(true);
        JFXDialogLayout content = new JFXDialogLayout();
        content.setStyle("-fx-background-color: #e0f7fa");
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().add(new Text("Leave group"));
        anchorPane.setStyle("-fx-background-color: #4dd0e1");
        content.setHeading(anchorPane);
        content.setBody(new Text("Are you sure?"));
        JFXButton bCancel = new JFXButton("No");
        JFXButton bOK = new JFXButton("Yes");
        buttonStyle(bOK);
        buttonStyle(bCancel);
        JFXDialog dialog = new JFXDialog(stackPaneForDialog, content, JFXDialog.DialogTransition.CENTER);
        bOK.setOnAction((event) -> {
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
            dialog.close();
        });
        ButtonBar buttonBar = new ButtonBar();
        buttonBar.getButtons().addAll(bOK, bCancel);
        content.setActions(buttonBar);
        stackPaneForDialog.setVisible(true);

        bCancel.setOnAction((event) -> {
            dialog.close();
            stackPaneForDialog.setVisible(false);
        });
        dialog.show();
        dialog.setOnDialogClosed((event) -> {
            dialog.show();
            stackPaneForDialog.setVisible(false);
        });
        initGroups(groupList);
    }
    void groupInfo(JFXListView<String> groupList) {
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
        Image icon = new Image("/ua/sumdu/java/lab2/messenger/images/"
                + "bubbles.png");
        stage.getIcons().add(icon);
        stage.setTitle("Group info");
        stage.setScene(new Scene(root, INFO_WIDTH, INFO_HEIGHT));
        stage.setResizable(false);
        stage.show();
    }
}
