package com.bp.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


public class Start
{
    //<editor-fold desc="属性">
    private static final int PORT = 8080;
    public static final String VERSION = "2.0";
    //</editor-fold>

    //<editor-fold desc="服务启动">
    public void bind(int port) throws InterruptedException
    {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try
        {
            ServerBootstrap server = new ServerBootstrap();
            server.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>()
                    {
                        @Override
                        protected void initChannel(SocketChannel ch)
                        {
                            ch.pipeline().addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(null))); // 最大长度
                            ch.pipeline().addLast(new ObjectEncoder());
                            ch.pipeline().addLast(new IdleStateHandler(0, 0, 10));
                            ch.pipeline().addLast(new SimpleServerHandler());

                        }
                    });
            ChannelFuture future = server.bind(port).sync();
            System.out.println("启动成功");
            heartbeat();
            future.channel().closeFuture().sync();

        }
        finally
        {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
    //</editor-fold>

    //<editor-fold desc="心跳">
    private static void heartbeat()
    {
        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            public void run()
            {
                Set<Map.Entry<ChannelId, ClientInfo>> entries = ClientInfo.channelIdBindClientInfoMap.entrySet();
                for (Map.Entry<ChannelId, ClientInfo> next : entries)
                {
                    ClientInfo clientInfo = next.getValue();
                    clientInfo.heartbeat();
                }
                System.out.println("连接数量>" + ClientInfo.channelIdBindClientInfoMap.size());
            }
        }, 1000 * 5, 1000 * 5);
    }
    //</editor-fold>

    public static void main(String[] args)
    {
        try
        {
            new Start().bind(PORT);
        }
        catch (InterruptedException e)
        {
            System.out.println("启动失败:" + e.getMessage());
            e.printStackTrace();
        }
    }
}
