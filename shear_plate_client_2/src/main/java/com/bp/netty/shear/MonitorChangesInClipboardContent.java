package com.bp.netty.shear;


import com.bp.netty.utils.action.Action;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 监控剪切板内容发生变化
 */
public class MonitorChangesInClipboardContent
{
    public Action<String> callbackText;
    public static String lastTextContent;
    public static String filePaths;
    public static long fileSize;

    public MonitorChangesInClipboardContent(Action<String> callbackText, Action<List<File>> callbackFiles)
    {
        this.callbackText = callbackText;
        ShearPlateApi.clear();
        new Timer().schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                String sysClipboardText = ShearPlateApi.getSysClipboardText();

                if (sysClipboardText != null && !"".equals(sysClipboardText))
                {
                    if (lastTextContent == null)
                    {
                        callbackText.callback(sysClipboardText);
                        lastTextContent = sysClipboardText;
                    }
                    else if (!lastTextContent.equals(sysClipboardText))
                    {
                        callbackText.callback(sysClipboardText);
                        lastTextContent = sysClipboardText;
                    }
                }

                List<File> sysClipboardFiles = ShearPlateApi.getSysClipboardFiles();
                if (sysClipboardFiles != null && sysClipboardFiles.size() > 0)
                {
                    long size = 0;
                    for (File file : sysClipboardFiles)
                    {
                        if (file.isFile())
                        {
                            size += file.length();
                        }
                        else
                        {
                            size += FileUtils.sizeOfDirectory(file);
                        }
                    }

                    if (size > 104857600)
                    {
                        System.out.println("文件过大");
                        return;
                    }

                    if (filePaths == null)
                    {
                        filePaths = sysClipboardFiles.toString();
                        fileSize = size;
                        callbackFiles.callback(sysClipboardFiles);
                    }
                    else if (!filePaths.equals(sysClipboardFiles.toString()) || fileSize != size)
                    {
                        filePaths = sysClipboardFiles.toString();
                        fileSize = size;
                        callbackFiles.callback(sysClipboardFiles);
                    }
                }
            }
        }, 0, 200);
    }
}
