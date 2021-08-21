package com.bp.protocol.handshake;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 握手协议
 */
@Data
@Getter
@ToString
public class HandshakeResult implements Serializable
{
    private boolean isSuccess;
}
