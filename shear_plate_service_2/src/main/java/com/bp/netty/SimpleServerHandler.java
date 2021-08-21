package com.bp.netty;


import com.bp.protocol.broadcast.*;
import com.bp.protocol.handshake.Handshake;
import com.bp.protocol.handshake.HandshakeResult;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


@SuppressWarnings("unused")
public class SimpleServerHandler extends SimpleChannelInboundHandler<Object>
{
    //<editor-fold desc="读取通道消息">
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg)
    {
        //<editor-fold desc="握手">
        if (msg instanceof Handshake)
        {
            System.out.println("收到客户端握手=====>");
            Handshake handshake = (Handshake) msg;

            HandshakeResult handshakeResult = new HandshakeResult();
            ClientInfo clientInfo = new ClientInfo();
            if (handshake.getVersion() != null && Start.VERSION.equals(handshake.getVersion()))
            {
                clientInfo = new ClientInfo();
                clientInfo.setCtx(ctx);
                clientInfo.addChannelIdBindClientInfoMap();
                handshakeResult.setSuccess(true);
                System.out.println("新的连接进来>" + ctx.channel().id() + "，共" + (ClientInfo.channelIdBindClientInfoMap.size() + 1));
            }
            clientInfo.send(handshakeResult);
        }
        //</editor-fold>

        //<editor-fold desc="广播文件消息">
        if (msg instanceof BroadcastFile)
        {
            System.out.println("收到客户端广播文件消息=====>");
            BroadcastFile broadcastFile = (BroadcastFile) msg;
            ClientInfo clientInfo = ClientInfo.channelIdBindClientInfoMap.get(ctx.channel().id());
            clientInfo.broadcast(broadcastFile);
        }
        //</editor-fold>

        //<editor-fold desc="修改广播通道">
        if (msg instanceof UpdateBroadcastChannel)
        {
            System.out.println("收到客户端修改广播通道=====>");
            UpdateBroadcastChannel updateBroadcastChannel = (UpdateBroadcastChannel) msg;
            ClientInfo clientInfo = ClientInfo.channelIdBindClientInfoMap.get(ctx.channel().id());
            clientInfo.setBroadcastSignal(updateBroadcastChannel.getBroadcastChannel());

            UpdateBroadcastChannelResult updateBroadcastChannelResult = new UpdateBroadcastChannelResult();
            updateBroadcastChannelResult.setSuccess(true);
            updateBroadcastChannelResult.setBroadcastChannel(updateBroadcastChannel.getBroadcastChannel());
            clientInfo.send(updateBroadcastChannelResult);
        }
        //</editor-fold>

        //<editor-fold desc="广播文本消息">
        if (msg instanceof BroadcastText)
        {
            System.out.println("收到客户端广播文本消息=====>");
            BroadcastText broadcastText = (BroadcastText) msg;
            ClientInfo clientInfo = ClientInfo.channelIdBindClientInfoMap.get(ctx.channel().id());
            clientInfo.broadcast(broadcastText);
            System.out.println(broadcastText);
        }
        //</editor-fold>


    }
    //</editor-fold>

    //<editor-fold desc="通道激活">
    @Override
    public void channelActive(ChannelHandlerContext ctx)
    {
        System.out.println("新的连接进来");
    }
    //</editor-fold>

    //<editor-fold desc="通道断开">
    @Override
    public void channelInactive(ChannelHandlerContext ctx)
    {
        System.out.println("通道断开");
        disconnected(ctx);
    }
    //</editor-fold>

    //<editor-fold desc="长时间没有通信">
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
    {
        ctx.flush();
        ctx.close();
    }
    //</editor-fold>

    //<editor-fold desc="连接断开">
    private void disconnected(ChannelHandlerContext ctx)
    {
        ClientInfo clientInfo = ClientInfo.channelIdBindClientInfoMap.get(ctx.channel().id());
        if (clientInfo != null)
        {
            clientInfo.close();
        }
        else
        {
            ctx.flush();
            ctx.close();
        }
    }
    //</editor-fold>
}
