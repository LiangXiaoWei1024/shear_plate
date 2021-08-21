package com.bp.protocol.broadcast;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 修改广播通道
 */
@Data
@Getter
@ToString
public class UpdateBroadcastChannel implements Serializable
{
    private String broadcastChannel;
}
