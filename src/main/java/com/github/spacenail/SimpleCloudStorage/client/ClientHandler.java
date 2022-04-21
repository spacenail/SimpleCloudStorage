package com.github.spacenail.SimpleCloudStorage.client;

import com.github.spacenail.SimpleCloudStorage.model.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ClientHandler extends SimpleChannelInboundHandler<CloudMessage> {
    private final MainController mainController;
    private final AuthController authController;
    private final RegController regController;


    public ClientHandler(MainController mainController, AuthController authController, RegController regController) {
        this.mainController = mainController;
        this.authController = authController;
        this.regController = regController;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CloudMessage message) throws Exception {
        switch (message.getMessageType()) {
            case FILE:
                FileMessage fileMessage = (FileMessage) message;
                System.out.println("FILE_MESSAGE " + fileMessage);
                Files.write(Paths.get(fileMessage.getPath())
                        .resolve(fileMessage.getName()), fileMessage.getBytes()
                );
                mainController.updateView(mainController.getClientDirectory());
                break;
            case LIST:
                ListMessage listMessage = (ListMessage) message;
                System.out.println("LIST_MESSAGE " + listMessage);
                mainController.updateView(listMessage);
                break;
            case AUTH_RESPONSE:
                AuthResponse authResponse = (AuthResponse) message;
                authController.auth(authResponse.isAuth());
                break;
            case REG_RESPONSE:
                RegResponse regResponse = (RegResponse) message;
                if (regResponse.isSuccessCreateUser()) {
                    regController.closeWindow();
                } else {
                    regController.showErrorMessage(regResponse.getMessage());
                }
                break;
        }
    }
}
