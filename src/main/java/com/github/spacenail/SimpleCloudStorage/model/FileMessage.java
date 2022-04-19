package com.github.spacenail.SimpleCloudStorage.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileMessage implements CloudMessage {
    private final byte[] bytes;
    private final String name;
    private final String path;

    public FileMessage(Path srcPath, String dstPath) throws IOException {
       bytes = Files.readAllBytes(srcPath);
       name = srcPath.getFileName().toString();
       this.path = dstPath;
    }

    public String getName() {
        return name;
    }

    public byte[] getBytes(){
        return bytes;
    }

    public String getPath() {
        return path;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.FILE;
    }
}
