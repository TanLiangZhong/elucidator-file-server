package com.sword.elucidator.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * file - vo
 *
 * @author Tan
 * @version 1.0 2021/1/17
 */
@Data
@Builder
@ApiModel("File-Vo")
public class FileUploadVo {
    
    @ApiModelProperty("文件Id")
    private String fileId;

    @ApiModelProperty("文件名称")
    private String name;

    @ApiModelProperty("文件大小")
    private Long size;

    @ApiModelProperty("文件类型")
    private String contentType;

    @ApiModelProperty("文件后缀")
    private String suffix;

    @ApiModelProperty("创建时间")
    private LocalDateTime gmtCreated;

    @ApiModelProperty("预览地址")
    private String previewUrl;

}
