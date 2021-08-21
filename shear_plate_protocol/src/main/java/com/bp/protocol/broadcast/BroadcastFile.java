package com.bp.protocol.broadcast;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 广播文件消息
 */
@Data
@Getter
@ToString
public class BroadcastFile implements Serializable
{
    private List<FileUploadEntity> fileUploadEntities;
    public String filePaths;
    public long fileSize;
}
