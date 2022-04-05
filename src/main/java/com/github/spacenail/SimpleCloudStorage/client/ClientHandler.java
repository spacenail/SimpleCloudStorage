package com.github.spacenail.SimpleCloudStorage.client;

import com.github.spacenail.SimpleCloudStorage.model.CloudMessage;
import com.github.spacenail.SimpleCloudStorage.model.FileMessage;
import com.github.spacenail.SimpleCloudStorage.model.ListMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ClientHandler extends SimpleChannelInboundHandler<CloudMessage> {
    private ClientController clientController;

    public void setClientController(ClientController clientController) {
        this.clientController = clientController;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CloudMessage message) throws Exception {
        switch (message.getMessageType()) {
            case FILE:
                FileMessage fileMessage = (FileMessage) message;
                Files.write(Paths.get(fileMessage.getPath())
                        .resolve(fileMessage.getName()), fileMessage.getBytes()
                );
                clientController.updateView(clientController.getClientDirectory());
                break;
            case LIST:
                ListMessage listMessage = (ListMessage) message;
                clientController.updateView(listMessage);
                break;
        }
    }
}
