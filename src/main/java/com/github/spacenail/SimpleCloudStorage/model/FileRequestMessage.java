package com.github.spacenail.SimpleCloudStorage.model;

public class FileRequestMessage implements CloudMessage {
    private final String name;

    public FileRequestMessage(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.FILE_REQUEST;
    }
}
