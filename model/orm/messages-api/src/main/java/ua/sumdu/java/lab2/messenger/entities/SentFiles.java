package ua.sumdu.java.lab2.messenger.entities;

import com.google.gson.GsonBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SentFiles {

  public ObservableList<FileCharacteristics> getList() {
    ObservableList<FileCharacteristics> list = FXCollections.observableArrayList();
    for (FileCharacteristics file : arrayList) {
      list.add(file);
    }
    return list;
  }

  List<FileCharacteristics> arrayList = new ArrayList<>();


  public ObservableList<FileCharacteristics> getObs() {
    return obs;
  }

  transient ObservableList<FileCharacteristics> obs = FXCollections.observableList(arrayList);

  public void updateObs() {
    obs = FXCollections.observableList(arrayList);
  }

  public void addFile(File file) {
    FileCharacteristics fileCharacteristics = new FileCharacteristics(file.getPath(), file.getName(), file.length());
    arrayList.add(fileCharacteristics);
  }

  public void remove(String name, long size) {
    for (FileCharacteristics file : arrayList) {
      if (name.equals(file.getName()) && size == file.getSize()) {
        arrayList.remove(file);
        obs.remove(file);
        return;
      }
    }
  }

  public String toJSonString() {
    return new GsonBuilder()
        .setPrettyPrinting()
        .create()
        .toJson(this);
  }

  public static SentFiles fromJson(String jsonString) {
    return new GsonBuilder()
        .setPrettyPrinting()
        .create()
        .fromJson(jsonString, SentFiles.class);
  }

  public int size() {
    return arrayList.size();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    SentFiles sentFiles = (SentFiles) obj;
    return Objects.equals(arrayList, sentFiles.arrayList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(arrayList);
  }

  public class FileCharacteristics {
    private final String path;
    private final String name;
    private final long size;
    private String shortSize;

    public String getShortSize() {
      return shortSize;
    }

    public void setShortSize(String shortSize) {
      this.shortSize = shortSize;
    }

    public String getName() {
      return name;
    }

    public long getSize() {
      return size;
    }

    public FileCharacteristics(String path, String name, long size) {
      this.path = path;
      this.name = name;
      this.size = size;
      shortSize = stringShortSize();
    }

    private String stringShortSize() {
      final int bytes = (int) size;
      final int kilobytes = bytes / 1024;
      final int megabytes = kilobytes / 1024;
      final int gigabytes = megabytes / 1024;
      final int terabytes = gigabytes / 1024;
      if (terabytes > 1) {
        return terabytes + "TB";
      }
      if (gigabytes > 1) {
        return gigabytes + "GB";
      }
      if (megabytes > 1) {
        return megabytes + "MB";
      }
      if (kilobytes > 1) {
        return kilobytes + "KB";
      } else {
        return bytes + "B";
      }
    }

    public String getPath() {
      return path;
    }

  }
}
