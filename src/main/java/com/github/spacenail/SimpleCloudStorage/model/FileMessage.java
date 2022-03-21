package com.github.spacenail.SimpleCloudStorage.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileMessage implements CloudMessage {
    private final byte[] bytes;
    private final int size;
    private final String name;

    public FileMessage(Path path) throws IOException {
       bytes = Files.readAllBytes(path);
       size = bytes.length;
       name = path.getFileName().toString();
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public byte[] getBytes(){
        return bytes;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.FILE;
    }
}
