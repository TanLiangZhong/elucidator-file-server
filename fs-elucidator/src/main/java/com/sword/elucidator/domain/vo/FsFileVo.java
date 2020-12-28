package com.sword.elucidator.domain.vo;

import com.mongodb.client.gridfs.model.GridFSFile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * file - vo
 *
 * @author Tan
 * @version 1.0 2020/12/27
 */
@Data
@ApiModel("File-Vo")
public class FsFileVo {

    @ApiModelProperty("Id")
    private String id;

    @ApiModelProperty("文件名称")
    private String name;

    @ApiModelProperty("文件类型")
    private String contentType;

    @ApiModelProperty("文件大小")
    private Long size;

    @ApiModelProperty("文件后缀")
    private String suffix;

    @ApiModelProperty("文件内容")
    private byte[] content;

    @ApiModelProperty("MD5 值")
    private String md5;

    @ApiModelProperty("创建时间")
    private LocalDateTime gmtCreated;

    @ApiModelProperty("预览地址")
    private String previewUrl;

    @ApiModelProperty(value = "gridFsFile", hidden = true)
    private GridFSFile gridFsFile;
}
