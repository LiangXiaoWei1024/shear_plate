package com.bp.netty.utils;

import com.alibaba.fastjson.JSONObject;

public class StringUtils
{
    public static boolean isJson(String str)
    {
        try{
            JSONObject.parseObject(str);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
