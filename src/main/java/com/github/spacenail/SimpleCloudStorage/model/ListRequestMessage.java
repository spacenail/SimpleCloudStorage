package com.github.spacenail.SimpleCloudStorage.model;

public class ListRequestMessage implements CloudMessage{
    private String pathRequest;

    public ListRequestMessage(String pathRequest){
        this.pathRequest = pathRequest;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.LIST_REQUEST;
    }
}
