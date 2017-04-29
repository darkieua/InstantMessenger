package ua.sumdu.java.lab2.messenger.transferring.impl;

import com.jfoenix.controls.JFXTextArea;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ua.sumdu.java.lab2.messenger.entities.SentFiles;

public class ReceivingFilesController {
    @FXML
    private JFXTextArea message;

    @FXML
    private TableView<SentFiles.FileCharacteristics> files;

    @FXML
    private TableColumn fileName;

    @FXML
    private TableColumn fileSize;

    @FXML
    private Button remote;

    private SentFiles sentFiles;

    private SentFiles newFileList;

    private String name;

    public final void initAfterAddParametrs() {
        Text text = new Text("User " + name + " sent you a request for data transfer.");
        text.wrappingWidthProperty().bind(message.widthProperty());
        message.setText(text.getText());
        message.setFocusTraversable(false);
        fileName.setCellValueFactory(
                new PropertyValueFactory<>("name"));
        fileSize.setCellValueFactory(
                new PropertyValueFactory<>("shortSize"));
        files.setItems(sentFiles.getObs());
        files.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldValue,
                 newValue) -> remote.setVisible(true));
    }


    public SentFiles getNewFileList() {
        return newFileList;
    }

    public void setSentFiles(SentFiles sentFiles) {
         this.sentFiles = sentFiles;
    }

    public void buttonCancel(ActionEvent actionEvent) {
        newFileList = new SentFiles();
        closeStage(actionEvent);
    }

    public void buttonRemote() {
       SentFiles.FileCharacteristics file = files.getSelectionModel().getSelectedItem();
       sentFiles.getList().remove(file);
       sentFiles.updateObs();
       files.setItems(sentFiles.getObs());
    }

    public void buttonOk(ActionEvent actionEvent) {
        newFileList = sentFiles;
        closeStage(actionEvent);
    }

    public void setName(String name) {
        this.name = name;
    }
        
    private void closeStage(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
