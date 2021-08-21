package com.bp.netty;

import com.bp.protocol.heartbeat.Heartbeat;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 * 客户端信息存储
 */
@Data
@Getter
@ToString
@SuppressWarnings("unused")
public class ClientInfo
{
    //<editor-fold desc="属性">
    public static Map<ChannelId, ClientInfo> channelIdBindClientInfoMap = new Hashtable<>();
    private ChannelHandlerContext ctx;
    private String broadcastSignal;
    //</editor-fold>

    //<editor-fold desc="心跳">
    public void heartbeat()
    {
        Heartbeat heartbeat = new Heartbeat();
        heartbeat.setTime(System.currentTimeMillis());
        send(heartbeat);
    }
    //</editor-fold>

    //<editor-fold desc="关闭通道">
    public void close()
    {
        removeClientInfoList();
        this.broadcastSignal = null;
        this.ctx.flush();
        this.ctx.close();
        this.ctx = null;
    }
    //</editor-fold>

    //<editor-fold desc="广播消息">
    public void broadcast(Object msg)
    {
        if (this.broadcastSignal == null)
            return;

        Set<Map.Entry<ChannelId, ClientInfo>> entries = channelIdBindClientInfoMap.entrySet();
        for (Map.Entry<ChannelId, ClientInfo> next : entries)
        {
            ClientInfo clientInfo = next.getValue();
            if (clientInfo.broadcastSignal == null)
                continue;
            if (this.broadcastSignal.equals(clientInfo.broadcastSignal))
            {
                if (this.ctx.channel().id() != clientInfo.ctx.channel().id())
                {
                    send(clientInfo.getCtx(), msg);
                }
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="发送消息">
    public void send(Object msg)
    {
        if (this.ctx != null && this.ctx.channel().isActive())
        {
            this.ctx.channel().writeAndFlush(msg);
        }
    }

    public void send(ChannelHandlerContext ctx, Object msg)
    {
        if (ctx != null && ctx.channel().isActive())
        {
            ctx.channel().writeAndFlush(msg);
        }
    }
    //</editor-fold>

    //<editor-fold desc="map操作">

    public void addChannelIdBindClientInfoMap()
    {
        synchronized (ClientInfo.class)
        {
            channelIdBindClientInfoMap.put(ctx.channel().id(), this);
        }
    }

    public void removeClientInfoList()
    {
        synchronized (ClientInfo.class)
        {
            channelIdBindClientInfoMap.remove(ctx.channel().id());
        }
    }
    //</editor-fold>
}
