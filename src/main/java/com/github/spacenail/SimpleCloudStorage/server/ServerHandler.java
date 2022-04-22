package com.github.spacenail.SimpleCloudStorage.server;

import com.github.spacenail.SimpleCloudStorage.model.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerHandler extends SimpleChannelInboundHandler<CloudMessage> {
    private final Path serverDirectory = Paths.get("ServerDirectory");
    private static final Logger log = LogManager.getLogger();
    private boolean isAuthorized;
    private final DataBase db;

    public ServerHandler(DataBase db) {
        this.db = db;
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        log.trace("Disconnected: " + ctx.channel().remoteAddress());
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        log.trace("Connected: " + ctx.channel().remoteAddress());
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CloudMessage cloudMessage) throws Exception {
        switch (cloudMessage.getMessageType()) {
            case FILE:
                log.trace("incoming FILE message");
                if (isAuthorized) {
                    FileMessage fileMessage = (FileMessage) cloudMessage;
                    Files.write(Paths.get(fileMessage
                                    .getPath())
                                    .resolve(fileMessage.getName())
                            , fileMessage.getBytes()
                    );
                    ctx.writeAndFlush(new ListMessage(
                                    Paths.get(fileMessage.getPath())
                            )
                    );
                    log.trace("send FILE_MESSAGE message");
                }
                break;
            case FILE_REQUEST:
                log.trace("incoming FILE_REQUEST message");
                if (isAuthorized) {
                    FileRequestMessage fileRequestMessage = (FileRequestMessage) cloudMessage;
                    Path path = Paths.get(fileRequestMessage.getReqPath());
                    ctx.writeAndFlush(new FileMessage(path, fileRequestMessage.getDstPath()));
                    log.trace("send FILE_MESSAGE message");
                }
                break;
            case LIST_REQUEST:
                log.trace("incoming LIST_REQUEST message");
                if (isAuthorized) {
                    ListRequestMessage listRequestMessage = (ListRequestMessage) cloudMessage;
                    if (listRequestMessage.getPath() == null) {
                        ctx.writeAndFlush(new ListMessage(serverDirectory));
                        log.trace("send LIST_MESSAGE message");
                    } else {
                        Path pathRequest = Paths.get(listRequestMessage.getPath(),
                                listRequestMessage.getResolve());
                        if (pathRequest.toAbsolutePath().toFile().isDirectory()) {
                            ctx.writeAndFlush(new ListMessage(pathRequest.toAbsolutePath()));
                            log.trace("send LIST_MESSAGE message");
                        }
                    }
                }
                break;
            case AUTH_REQUEST:
                AuthRequest authRequest = (AuthRequest) cloudMessage;
                log.trace("incoming AUTH_REQUEST message " + authRequest);
                verify(authRequest);
                ctx.writeAndFlush(new AuthResponse(isAuthorized));
                log.trace("send AUTH_RESPONSE message " + isAuthorized);
                break;
            case REG_REQUEST:
                RegRequest regRequest = (RegRequest) cloudMessage;
                log.trace("incoming REG_REQUEST message " + regRequest);
                if (db.select(regRequest.getUsername())) {
                    ctx.writeAndFlush(new RegResponse(false, "Username is already exist!"));
                } else {
                    boolean success = db.insert(regRequest.getUsername(), regRequest.getPassword());
                    if (success) {
                        ctx.writeAndFlush(new RegResponse(true));
                    } else {
                        ctx.writeAndFlush(new RegResponse(false, "User creation error"));
                    }
                }
        }
    }

    private void verify(AuthRequest authRequest) {
        isAuthorized = db.select(
                authRequest.getLogin(),
                authRequest.getPassword());
    }
}