package com.github.spacenail.SimpleCloudStorage.client;

import com.github.spacenail.SimpleCloudStorage.model.CloudMessage;
import com.github.spacenail.SimpleCloudStorage.model.FileMessage;
import com.github.spacenail.SimpleCloudStorage.model.ListMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Network implements Runnable{
    private ChannelFuture channelFuture;
    private final Bootstrap bootstrap;
    private final EventLoopGroup executors = new NioEventLoopGroup(1);
    private ChannelHandlerContext ctx;


    public Network(ClientController controller) {

        bootstrap = new Bootstrap();
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
                                controller.updateView(controller.getClientDirectory());
                                break;
                            case LIST:
                                ListMessage listMessage = (ListMessage) message;
                                controller.updateView(listMessage);
                                break;
                        }
                    }
                });
            }
        });
    }

    public ChannelHandlerContext getContext(){
        return ctx;
    }


    @Override
    public void run() {
    try {
        channelFuture = bootstrap.connect("localhost", 8189).sync();
        ctx = channelFuture.channel().pipeline().context("logic");
        channelFuture.channel().closeFuture().sync();
    } catch (InterruptedException e){
        e.printStackTrace();
    }finally {
        channelFuture.channel().close();
        executors.shutdownGracefully();
    }
    }
}
