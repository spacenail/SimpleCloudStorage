package com.github.spacenail.SimpleCloudStorage.client;

import com.github.spacenail.SimpleCloudStorage.model.CloudMessage;
import com.github.spacenail.SimpleCloudStorage.model.FileMessage;
import com.github.spacenail.SimpleCloudStorage.model.ListMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ClientHandler extends SimpleChannelInboundHandler<CloudMessage> {
    private final ClientController clientController;

    public ClientHandler(ClientController clientController) {
        this.clientController = clientController;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CloudMessage message) throws Exception {
        switch (message.getMessageType()) {
            case FILE:
                FileMessage fileMessage = (FileMessage) message;
                System.out.println("FILE_MESSAGE "+ fileMessage);
                Files.write(Paths.get(fileMessage.getPath())
                        .resolve(fileMessage.getName()), fileMessage.getBytes()
                );
                clientController.updateView(clientController.getClientDirectory());
                break;
            case LIST:
                ListMessage listMessage = (ListMessage) message;
                System.out.println("LIST_MESSAGE "+ listMessage);
                clientController.updateView(listMessage);
                break;
        }
    }
}
