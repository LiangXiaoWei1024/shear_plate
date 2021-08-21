package com.bp.protocol.broadcast;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@Data
@Getter
@ToString
public class FileUploadEntity implements Serializable
{
    private String path;
    private String name;
    private byte[] bytes;
}
