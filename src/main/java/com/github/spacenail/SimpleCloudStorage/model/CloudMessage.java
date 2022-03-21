package com.github.spacenail.SimpleCloudStorage.model;

import java.io.Serializable;

public interface CloudMessage extends Serializable {
    MessageType getMessageType();
}
