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
public class Handshake implements Serializable
{
    private String version;
}
