package com.bp.protocol.utils;

import com.bp.protocol.broadcast.FileUploadEntity;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class MyFileUtils
{
    //<editor-fold desc="获取文件下所有文件">
    public static void getFiles(String cataloguePath, List<FileUploadEntity> files, String rootPath) throws UnsupportedEncodingException
    {
        File file = new File(cataloguePath);
        getFiles(file, files, rootPath);
    }

    public static void getFiles(File file, List<FileUploadEntity> files, String rootPath) throws UnsupportedEncodingException
    {
        File[] tempList = file.listFiles();

        if (rootPath == null)
        {
            rootPath = file.getParent();
            if (!OsUtils.isMac())
            {
                rootPath = rootPath.replace("\\", "/");
            }
        }

        if (tempList == null || tempList.length < 1)
        {
            FileUploadEntity fileInfo = new FileUploadEntity();

            if (file.isFile())
            {
                if (!file.getName().equals(".DS_Store"))
                {
                    if (file.getParent().split(rootPath).length < 2)
                    {
                        fileInfo.setPath("/");
                    }
                    else
                    {
                        String path = file.getParent().replace("\\", "/").split(rootPath)[1];
                        fileInfo.setPath(URLEncoder.encode(path, "UTF-8"));
                    }
                    fileInfo.setName(URLEncoder.encode(file.getName(), "UTF-8"));
                    fileInfo.setBytes(MyFileUtils.getBytesByFile(file));
                }
            }
            else
            {
                if (file.getParent().replace("\\", "/").split(rootPath).length < 2)
                {
                    fileInfo.setPath(URLEncoder.encode(file.getName(), "UTF-8"));
                }
                else
                {
                    String path = file.getParent().replace("\\", "/").split(rootPath)[1];
                    fileInfo.setPath(URLEncoder.encode(path + "/" + file.getName(), "UTF-8"));
                }
            }
            files.add(fileInfo);
            return;
        }

        for (File value : tempList)
        {
            FileUploadEntity fileInfo = new FileUploadEntity();

            if (value.isFile())
            {
                if (!value.getName().equals(".DS_Store"))
                {
                    String path = value.getParent().replace("\\", "/").split(rootPath)[1];
                    fileInfo.setPath(URLEncoder.encode(path, "UTF-8"));
                    fileInfo.setName(URLEncoder.encode(value.getName(), "UTF-8"));
                    fileInfo.setBytes(MyFileUtils.getBytesByFile(value));
                    files.add(fileInfo);
                }
            }
            if (value.isDirectory())
            {
                String path = value.getParent().replace("\\", "/").split(rootPath)[1];
                fileInfo.setPath(URLEncoder.encode(path + "/" + value.getName(), "UTF-8"));
                files.add(fileInfo);
                getFiles(value.getPath(), files, rootPath);
            }

        }
    }
    //</editor-fold>

    //<editor-fold desc="获取文件大小">
    public static long getFileSize(File file)
    {
        return FileUtils.sizeOfDirectory(file);
    }
    //</editor-fold>

    //<editor-fold desc="将文件转换成Byte数组">
    public static byte[] getBytesByFile(String pathStr)
    {
        return getBytesByFile(new File(pathStr));
    }

    public static byte[] getBytesByFile(File file)
    {
        try
        {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(8192);
            byte[] b = new byte[8192];
            int n;
            while ((n = fis.read(b)) != -1)
            {
                bos.write(b, 0, n);
            }
            fis.close();
            byte[] data = bos.toByteArray();
            bos.close();
            return data;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold desc="将Byte数组转换成文件">
    public static void bytesToFile(byte[] bytes, String filePath, String fileName)
    {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file;
        try
        {
            File dir = new File(filePath);
            if (!dir.exists())
            {// 判断文件目录是否存在
                boolean mkdirs = dir.mkdirs();
            }
            if (fileName != null && fileName.length() > 0 && bytes.length > 0)
            {
                file = new File(filePath + fileName);
                fos = new FileOutputStream(file);
                bos = new BufferedOutputStream(fos);
                bos.write(bytes);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (bos != null)
            {
                try
                {
                    bos.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if (fos != null)
            {
                try
                {
                    fos.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="清空临时目录">
    public static void delAllFile(String dir)
    {
        delAllFile(new File(dir));
    }

    public static void delAllFile(File file)
    {
        if (!file.exists())
        {
            return;
        }
        File[] files = file.listFiles();
        if (files != null)
        {
            for (File del : files)
            {
                if (del.isFile())
                {
                    boolean delete = del.delete();
                }
                if (del.isDirectory())
                {
                    if (Objects.requireNonNull(del.listFiles()).length < 1)
                    {
                        boolean delete = del.delete();
                    }
                    else
                    {
                        delAllFile(del);
                    }
                }
                boolean delete = del.delete();
            }
        }
    }
    //</editor-fold>
}
