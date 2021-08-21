package com.bp.netty;


import com.bp.netty.page.Panel;
import com.bp.netty.shear.MonitorChangesInClipboardContent;
import com.bp.netty.socket.ClientInfo;
import com.bp.protocol.broadcast.BroadcastFile;
import com.bp.protocol.broadcast.BroadcastText;
import com.bp.protocol.broadcast.FileUploadEntity;
import com.bp.protocol.utils.MyFileUtils;


import javax.swing.*;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class Start
{
    public static ClientInfo clientInfo;
    private static final String IP = "192.168.1.75";
    private static final int PORT = 8080;
    public static final String VERSION = "2.0";

    private static void start()
    {
        SwingUtilities.invokeLater(() -> new Panel().createGUI());

        clientInfo = new ClientInfo(IP, PORT);
        clientInfo.start();

        new MonitorChangesInClipboardContent(textMsg ->
        {
            BroadcastText broadcastText = new BroadcastText();
            try
            {
                broadcastText.setText(URLEncoder.encode(textMsg, "UTF-8"));
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
            clientInfo.send(broadcastText);
        }, fileList ->
        {
            List<FileUploadEntity> fileInfos = new ArrayList<>();
            for (File file : fileList)
            {
                try
                {
                    MyFileUtils.getFiles(file, fileInfos, null);
                }
                catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
            }
            BroadcastFile broadcastFile = new BroadcastFile();
            broadcastFile.setFileUploadEntities(fileInfos);
            try
            {
                broadcastFile.setFilePaths(URLEncoder.encode(MonitorChangesInClipboardContent.filePaths,"UTF-8"));
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
            broadcastFile.setFileSize(MonitorChangesInClipboardContent.fileSize);
            System.out.println("发送。。。。。。。。。。。。。。。");
            clientInfo.send(broadcastFile);
        });
    }

    public static void main(String[] args)
    {
        start();
    }

}
