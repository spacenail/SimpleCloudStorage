package com.github.spacenail.SimpleCloudStorage.model;

public class FileRequestMessage implements CloudMessage {
    private final String reqPath;
    private final String dstPath;

    public FileRequestMessage(String reqPath, String dstPath) {
        this.reqPath = reqPath;
        this.dstPath = dstPath;
    }

    public String getReqPath() {
        return reqPath;
    }

    public String getDstPath() {
        return dstPath;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.FILE_REQUEST;
    }
}
