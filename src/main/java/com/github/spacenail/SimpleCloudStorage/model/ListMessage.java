package com.github.spacenail.SimpleCloudStorage.model;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class ListMessage implements CloudMessage{
    private final List<String> list;
    private final String path;

    public ListMessage(Path path) {
        list = Arrays.asList(path.toFile().list());
        this.path = path.toString();
    }
    public String getPath() {
        return path;
    }

    public List<String> getList() {
        return list;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.LIST;
    }
}
