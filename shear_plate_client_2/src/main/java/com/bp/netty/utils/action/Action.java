package com.bp.netty.utils.action;

public interface Action<T>
{
    void callback(T t);
}
