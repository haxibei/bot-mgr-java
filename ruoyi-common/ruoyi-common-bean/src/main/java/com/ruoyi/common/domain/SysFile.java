package com.ruoyi.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 文件信息
 * 
 * @author ruoyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysFile
{
    private Long fileId;

    private Long fileIIndex;

    private String originalName;


    /**
     * 文件名称
     */
    private String name;

    /**
     * 文件地址
     */
    private String url;

    public SysFile(Long fileId, String name, String url) {
        this.fileId = fileId;
        this.name = name;
        this.url = url;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("fileId", getFileId())
            .append("name", getName())
            .append("url", getUrl())
            .toString();
    }
}
