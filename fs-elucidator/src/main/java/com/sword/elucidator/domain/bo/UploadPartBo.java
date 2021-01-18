package com.sword.elucidator.domain.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * file - vo
 *
 * @author Tan
 * @version 1.0 2020/12/27
 */
@Data
@ApiModel("File-Vo")
public class UploadPartBo {

    @ApiModelProperty("文件Id,第一片上传后会返回,后续片段必须带上")
    private String fileId;

    @ApiModelProperty("文件名称,第一片上传必须带上")
    private String name;

    @ApiModelProperty("文件大小,第一片上传必须带上")
    private Long size;

    @ApiModelProperty("文件后缀,第一片上传必须带上")
    private String suffix;

    @ApiModelProperty(value = "分片块大小", required = true)
    private Integer chunkSize;

    @ApiModelProperty(value = "分片块索引, 从零开始", required = true)
    private Integer chunkIndex;

    @ApiModelProperty(value = "文件", required = true)
    private MultipartFile file;

    @Override
    public String toString() {
        return "UploadPartBo{" +
                "fileId='" + fileId + '\'' +
                ", name='" + name + '\'' +
                ", size=" + size +
                ", suffix='" + suffix + '\'' +
                ", chunkSize=" + chunkSize +
                ", chunkIndex=" + chunkIndex +
                '}';
    }
}
