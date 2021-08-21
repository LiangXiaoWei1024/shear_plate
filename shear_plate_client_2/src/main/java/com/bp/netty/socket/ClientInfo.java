package com.bp.netty.socket;

import com.bp.netty.Start;
import com.bp.protocol.broadcast.UpdateBroadcastChannel;
import com.bp.protocol.handshake.Handshake;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.util.concurrent.TimeUnit;

@Data
@Getter
@ToString
public class ClientInfo
{
    //<editor-fold desc="变量">
    public String broadcastSignal;
    private Bootstrap bootstrap;
    private Channel channel;
    private String ip;
    private int port;
    //</editor-fold>

    //<editor-fold desc="构造">
    public ClientInfo(String ip, int port)
    {
        this.ip = ip;
        this.port = port;
    }
    //</editor-fold>

    //<editor-fold desc="启动">
    public void start()
    {
        bootstrap = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();

        bootstrap.group(group);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);

        //第3步 给NIoSocketChannel初始化handler， 处理读写事件
        bootstrap.handler(new ChannelInitializer<NioSocketChannel>()
        {  //通道是NioSocketChannel
            @Override
            protected void initChannel(NioSocketChannel ch)
            {
                ch.pipeline().addLast(new ObjectEncoder());
                ch.pipeline().addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(null))); // 最大长度
                ch.pipeline().addLast(new IdleStateHandler(0, 0, 10));
                ch.pipeline().addLast(new SimpleClientHandler());
            }
        });
        doConnect();
    }
    //</editor-fold>

    //<editor-fold desc="连接">
    protected void doConnect()
    {
        if (this.channel != null && this.channel.isActive())
            return;

        ChannelFuture future = bootstrap.connect(ip, port);

        future.addListener((ChannelFutureListener) futureListener ->
        {
            if (futureListener.isSuccess())
            {
                this.channel = futureListener.channel();
            }
            else
            {
                futureListener.channel().eventLoop().schedule(this::doConnect, 5, TimeUnit.SECONDS);
            }
        });
    }
    //</editor-fold>

    //<editor-fold desc="发送消息">
    public void send(Object msg)
    {
        if (this.channel == null || !this.channel.isActive())
            return;

        this.channel.writeAndFlush(msg);
    }
    //</editor-fold>

    //<editor-fold desc="修改广播通道">
    public void updateBroadcastSignal(String broadcastChannel)
    {
        UpdateBroadcastChannel updateBroadcastChannel = new UpdateBroadcastChannel();
        updateBroadcastChannel.setBroadcastChannel(broadcastChannel);
        send(updateBroadcastChannel);
    }
    //</editor-fold>

    //<editor-fold desc="握手">
    public void handshake()
    {
        Handshake handshake = new Handshake();
        handshake.setVersion(Start.VERSION);
        send(handshake);
    }
    //</editor-fold>
}
