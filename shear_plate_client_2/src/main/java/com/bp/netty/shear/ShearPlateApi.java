package com.bp.netty.shear;


import com.bp.protocol.utils.OsUtils;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unused", "unchecked"})
public class ShearPlateApi
{
    //<editor-fold desc="从剪切板获取文字">
    public static String getSysClipboardText()
    {
        String ret = null;
        Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
        // 获取剪切板中的内容
        Transferable clipTf = sysClip.getContents(null);


        if (clipTf != null)
        {
            // 检查内容是否是文本类型
            if (clipTf.isDataFlavorSupported(DataFlavor.stringFlavor))
            {

                if (OsUtils.isMac())
                {
                    try
                    {
                        DataFlavor[] transferDataFlavors = clipTf.getTransferDataFlavors();
                        if (transferDataFlavors[0].getMimeType().equals("application/x-java-serialized-object; class=java.lang.String") ||
                                transferDataFlavors[0].getMimeType().equals("text/html; document=selection; class=java.io.Reader; charset=Unicode")

                        )
                        {
                            ret = (String) clipTf
                                    .getTransferData(DataFlavor.stringFlavor);
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
                else
                {
                    try
                    {
                        ret = (String) clipTf
                                .getTransferData(DataFlavor.stringFlavor);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        return ret;
    }
    //</editor-fold>

    //<editor-fold desc="设置剪切板文字内容">
    public static void setIntoClipboardText(String text)
    {
        try
        {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(text), null);
        }
        catch (Exception ex)
        {
            System.out.println("设置剪切板内容失败>:" + text);
            ex.printStackTrace();
        }
    }
    //</editor-fold>

    //<editor-fold desc="清空剪切板">
    public static void clear()
    {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(null), null);
    }
    //</editor-fold>

    //<editor-fold desc="从剪切板获取文件">
    public static List<File> getSysClipboardFiles()
    {
        Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
        // 获取剪切板中的内容
        Transferable clipTf = sysClip.getContents(null);
        if (clipTf.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
        {
            try
            {
                try
                {
                    return (List<File>) clipTf.getTransferData(DataFlavor.javaFileListFlavor);
                }
                catch (UnsupportedFlavorException e)
                {
                    e.printStackTrace();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        }
        return null;
    }
    //</editor-fold>

    //<editor-fold desc="设置文件到剪切板">
    public static void setIntoClipboardFile(File file)
    {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        Transferable contents = new Transferable()
        {
            final DataFlavor[] dataFlavors = new DataFlavor[]{DataFlavor.javaFileListFlavor};

            @Override
            public Object getTransferData(DataFlavor flavor)
            {
                List<File> fileList = new ArrayList<>();
                File[] files = file.listFiles();
                if (files != null && files.length > 0)
                {
                    Collections.addAll(fileList, files);
                }
                return fileList;
            }

            @Override
            public DataFlavor[] getTransferDataFlavors()
            {
                return dataFlavors;
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor)
            {
                for (DataFlavor dataFlavor : dataFlavors)
                {
                    if (dataFlavor.equals(flavor))
                    {
                        return true;
                    }
                }
                return false;
            }
        };

        clipboard.setContents(contents, null);
    }
    //</editor-fold>
}
