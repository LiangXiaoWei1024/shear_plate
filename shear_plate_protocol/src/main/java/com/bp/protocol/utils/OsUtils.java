package com.bp.protocol.utils;


public class OsUtils
{
    private static final String property = System.getProperties().getProperty("user.home");

    private static final String path = "/Desktop/云拷贝";

    //<editor-fold desc="是不是mac系统">
    public static boolean isMac()
    {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("mac");
    }
    //</editor-fold>

    //<editor-fold desc="获取临时目录">
    public static String getTemporaryDirectory()
    {
        return property+path;
    }
    //</editor-fold>
}
