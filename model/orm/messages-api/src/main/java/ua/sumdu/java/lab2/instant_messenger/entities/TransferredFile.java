package ua.sumdu.java.lab2.instant_messenger.entities;

import java.util.Arrays;
import java.util.Objects;

public class TransferredFile {
    private String type;
    private Byte[] file;
    private String name;

    public TransferredFile(String type, Byte[] file, String name) {

        this.type = type;
        this.file = file.clone();
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public TransferredFile setType(String type) {
        this.type = type;
        return this;
    }

    public Byte[] getFile() {
        return file.clone();
    }

    public TransferredFile setFile(Byte[] file) {
        this.file = file.clone();
        return this;
    }

    public String getName() {
        return name;
    }

    public TransferredFile setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TransferredFile that = (TransferredFile) obj;
        return Objects.equals(type, that.type) &&
                Arrays.equals(file, that.file) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, file, name);
    }
}
