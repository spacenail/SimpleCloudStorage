package com.github.spacenail.SimpleCloudStorage.model;

public class ListRequestMessage implements CloudMessage{
    private String path;
    private String resolve;

    public String getPath() {
        return path;
    }

    public String getResolve() {
        return resolve;
    }

    public ListRequestMessage(String path, String resolve){
        this.path = path;
        this.resolve = resolve;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.LIST_REQUEST;
    }
}
