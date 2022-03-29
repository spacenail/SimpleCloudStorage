package com.github.spacenail.SimpleCloudStorage.client;

import com.github.spacenail.SimpleCloudStorage.model.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class ClientController implements Initializable {
    @FXML
    private ListView<String> clientView;
    @FXML
    private ListView<String> serverView;
    @FXML
    private TextField clientPath;
    @FXML
    private TextField serverPath;
    private Path clientDirectory;
    private ChannelHandlerContext ctx;

    public void download() {
        ctx.writeAndFlush(new FileRequestMessage(
                getPath(serverPath.getText(), serverView.getSelectionModel().getSelectedItem())
                        .normalize().toAbsolutePath().toString(),
                clientPath.getText())
        );
    }

    public void upload() throws IOException {
        ctx.writeAndFlush(new FileMessage(
                getPath(clientPath.getText(), clientView.getSelectionModel().getSelectedItem()),
                serverPath.getText())
        );
    }

    private Path getPath(String dir, String file) {
        return Paths.get(dir).resolve(file);
    }

    @FXML
    private void close() {
        Platform.exit();
    }

    public void updateView(Path path) {
        Platform.runLater(() -> {
            clientPath.setText(path.toAbsolutePath().toString());
            clientView.getItems().clear();
            clientView.getItems().add("...");
            clientView.getItems().addAll(path.toFile().list());
        });
    }

    public void updateView(ListMessage listMessage) {
        Platform.runLater(() -> {
            serverPath.setText(listMessage.getPath());
            serverView.getItems().clear();
            serverView.getItems().add("...");
            serverView.getItems().addAll(listMessage.getList());
        });
    }

    private void clientViewHandler(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
            String item = clientView.getSelectionModel().getSelectedItem();
            if ("...".equals(item)) {
                if (clientDirectory.getParent() != null) {
                    clientDirectory = clientDirectory.getParent();
                }
            } else if (clientDirectory.resolve(item).toFile().isDirectory()) {
                clientDirectory = clientDirectory.resolve(item);
            }
            updateView(clientDirectory);
        }
    }

    private void serverViewHandler(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
            String item = serverView.getSelectionModel().getSelectedItem();
            if ("...".equals(item)) {
                ctx.writeAndFlush(new ListRequestMessage(serverPath.getText(), ".."));
            } else if (item != null) {
                ctx.writeAndFlush(new ListRequestMessage(serverPath.getText(), item));
            }
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        EventLoopGroup executors = new NioEventLoopGroup(1);
        try {
            ChannelFuture channelFuture = networkInit(executors).connect("localhost", 8189).sync();
            ctx = channelFuture.channel().pipeline().context("logic");
            clientDirectory = Paths.get("ClientDirectory").toAbsolutePath();
            clientView.setOnMouseClicked(this::clientViewHandler);
            serverView.setOnMouseClicked(this::serverViewHandler);
            updateView(clientDirectory);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            executors.shutdownGracefully();
        }
    }

    private Bootstrap networkInit(EventLoopGroup executors) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(executors);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) {
                socketChannel.pipeline().addFirst("decoder", new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                socketChannel.pipeline().addLast("encoder", new ObjectEncoder());
                socketChannel.pipeline().addLast("logic", new SimpleChannelInboundHandler<CloudMessage>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, CloudMessage message) throws Exception {
                        switch (message.getMessageType()) {
                            case FILE:
                                FileMessage fileMessage = (FileMessage) message;
                                Files.write(Paths.get(fileMessage.getPath())
                                        .resolve(fileMessage.getName()), fileMessage.getBytes()
                                );
                                updateView(clientDirectory);
                                break;
                            case LIST:
                                ListMessage listMessage = (ListMessage) message;
                                updateView(listMessage);
                                break;
                        }
                    }
                });
            }
        });
        return bootstrap;
    }
}