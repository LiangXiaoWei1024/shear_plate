package com.bp.protocol.heartbeat;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 心跳
 */
@Data
@Getter
@ToString
public class HeartbeatResult implements Serializable
{
    private long time;
}
