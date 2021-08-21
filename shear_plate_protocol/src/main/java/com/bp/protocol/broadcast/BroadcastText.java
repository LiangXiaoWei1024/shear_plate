package com.bp.protocol.broadcast;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 广播文本消息
 */
@Data
@Getter
@ToString
public class BroadcastText implements Serializable
{
    private String text;
}
