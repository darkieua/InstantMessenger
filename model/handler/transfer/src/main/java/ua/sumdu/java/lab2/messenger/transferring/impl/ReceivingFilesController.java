package ua.sumdu.java.lab2.messenger.transferring.impl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ua.sumdu.java.lab2.messenger.entities.SentFiles;

public class ReceivingFilesController {
  @FXML
  public Label message;

  @FXML
  public TableView files;

  @FXML
  public TableColumn fileName;

  @FXML
  public TableColumn fileSize;

  private SentFiles sentFiles;

  private SentFiles newFileList;

  private String name;


  public SentFiles getNewFileList() {
    return newFileList;
  }

  public void setSentFiles(SentFiles sentFiles) {
    this.sentFiles = sentFiles;
  }

  public void buttonCancel() {
    newFileList = new SentFiles();
  }

  public void buttonRemote(ActionEvent actionEvent) {
    System.out.println(sentFiles.toString());
  }

  public void buttonOk(ActionEvent actionEvent) {
    System.out.println(name);
  }

  public void setName(String name) {
    this.name = name;
  }
}
