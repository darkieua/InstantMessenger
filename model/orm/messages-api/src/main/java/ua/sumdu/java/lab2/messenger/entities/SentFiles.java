package ua.sumdu.java.lab2.messenger.entities;

import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class SentFiles {

  public List<FileCharacteristics> getList() {
    return list;
  }

  List<FileCharacteristics> list = new LinkedList<>();

  public void setList(List<FileCharacteristics> list) {
    this.list = list;
  }

  public void addFile(File file) {
    FileCharacteristics fileCharacteristics = new FileCharacteristics(file.getPath(), file.getName(), file.length());
    list.add(fileCharacteristics);
  }

  public void remove(String name, long size) {
    for (FileCharacteristics file : list) {
      if (name.equals(file.getName()) && size == file.getSize()) {
        list.remove(file);
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
    return list.size();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    SentFiles sentFiles = (SentFiles) obj;
    return Objects.equals(list, sentFiles.list);
  }

  @Override
  public int hashCode() {
    return Objects.hash(list);
  }

  public class FileCharacteristics {
    private final String path;
    private final String name;
    private final long size;

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
    }

    public String getPath() {
      return path;
    }

  }
}
