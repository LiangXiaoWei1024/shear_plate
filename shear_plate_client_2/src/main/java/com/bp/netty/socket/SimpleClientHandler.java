package com.bp.netty.socket;


import com.bp.netty.Start;
import com.bp.netty.page.Panel;
import com.bp.netty.shear.MonitorChangesInClipboardContent;
import com.bp.netty.shear.ShearPlateApi;
import com.bp.protocol.broadcast.BroadcastFile;
import com.bp.protocol.broadcast.BroadcastText;
import com.bp.protocol.broadcast.FileUploadEntity;
import com.bp.protocol.broadcast.UpdateBroadcastChannelResult;
import com.bp.protocol.handshake.HandshakeResult;
import com.bp.protocol.heartbeat.Heartbeat;
import com.bp.protocol.heartbeat.HeartbeatResult;
import com.bp.protocol.utils.MyFileUtils;
import com.bp.protocol.utils.OsUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import javax.swing.*;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;


public class SimpleClientHandler extends SimpleChannelInboundHandler<Object>
{
    //<editor-fold desc="接收通道消息">
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException
    {
        //<editor-fold desc="握手">
        if (msg instanceof HandshakeResult)
        {
            HandshakeResult handshakeResult = (HandshakeResult) msg;
            if (handshakeResult.isSuccess())
            {
                System.out.println("连接成功...");
            }
            else
            {
                System.out.println("连接失败...");
            }
        }
        //</editor-fold>

        //<editor-fold desc="心跳">
        if (msg instanceof Heartbeat)
        {
            Heartbeat heartbeat = (Heartbeat) msg;
            HeartbeatResult heartbeatResult = new HeartbeatResult();
            heartbeatResult.setTime(heartbeat.getTime());
            Start.clientInfo.send(heartbeatResult);
        }
        //</editor-fold>

        //<editor-fold desc="修改广播信号">
        if (msg instanceof UpdateBroadcastChannelResult)
        {
            UpdateBroadcastChannelResult updateBroadcastChannelResult = (UpdateBroadcastChannelResult) msg;
            if (updateBroadcastChannelResult.isSuccess())
            {
                Panel.label.setText(updateBroadcastChannelResult.getBroadcastChannel());
                new Thread(() -> JOptionPane.showMessageDialog(null, "广播信号切换成功", "", JOptionPane.PLAIN_MESSAGE)).start();
            }
            else
            {
                new Thread(() -> JOptionPane.showMessageDialog(null, "广播信号切换失败", "", JOptionPane.PLAIN_MESSAGE)).start();
            }
        }
        //</editor-fold>

        //<editor-fold desc="广播文本信息">
        if (msg instanceof BroadcastText)
        {
            BroadcastText broadcastText = (BroadcastText) msg;
            try
            {
                broadcastText.setText(URLDecoder.decode(broadcastText.getText(), "UTF-8"));
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
            MonitorChangesInClipboardContent.lastTextContent = broadcastText.getText();
            ShearPlateApi.setIntoClipboardText(broadcastText.getText());
        }
        //</editor-fold>

        //<editor-fold desc="广播文件消息">
        if (msg instanceof BroadcastFile)
        {
            System.out.println("收到广播文件消息");
            BroadcastFile broadcastFile = (BroadcastFile) msg;
            //获取临时存储目录
            String temporaryDirectory = OsUtils.getTemporaryDirectory();
            //清空临时目录
            MyFileUtils.delAllFile(temporaryDirectory);

            List<FileUploadEntity> fileUploadEntities = broadcastFile.getFileUploadEntities();
            for (FileUploadEntity fileUploadEntity : fileUploadEntities)
            {

                if (fileUploadEntity.getPath() != null)
                {
                    fileUploadEntity.setPath(URLDecoder.decode(fileUploadEntity.getPath(), "UTF-8"));
                }
                if (fileUploadEntity.getName() != null)
                {
                    fileUploadEntity.setName(URLDecoder.decode(fileUploadEntity.getName(), "UTF-8"));
                }
//                    System.out.println(temporaryDirectory + "/" + fileUploadEntity.getPath()+"/"+ fileUploadEntity.getName());
                MyFileUtils.bytesToFile(fileUploadEntity.getBytes(), temporaryDirectory + "/" + fileUploadEntity.getPath() + "/", fileUploadEntity.getName());
            }

            broadcastFile.setFilePaths(URLDecoder.decode(broadcastFile.getFilePaths(), "UTF-8"));
            //mac系统自带剪切板用不了//找到mac设置文件剪切可以放开
/*//            MonitorChangesInClipboardContent.filePaths = broadcastFile.getFilePaths();
//            MonitorChangesInClipboardContent.fileSize = broadcastFile.getFileSize();
//            ShearPlateApi.setIntoClipboardFile(new File(temporaryDirectory));*/
        }
        //</editor-fold>
    }
    //</editor-fold>

    //<editor-fold desc="通道连接激活">
    @Override
    public void channelActive(ChannelHandlerContext ctx)
    {
        System.out.println("启动成功...");
        //与服务器握手 确认版本
        Start.clientInfo.handshake();
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
        System.out.println("长时间没有通信断开");
        //断开会自动调用通道断开方法
        ctx.close();
    }
    //</editor-fold>

    //<editor-fold desc="连接断开处理">
    private void disconnected(ChannelHandlerContext ctx)
    {
        ctx.flush();
        ctx.close();
        Start.clientInfo.setChannel(null);
        Start.clientInfo.doConnect();
    }
    //</editor-fold>
}
