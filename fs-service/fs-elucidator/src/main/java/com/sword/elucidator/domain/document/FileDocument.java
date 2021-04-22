package com.sword.elucidator.domain.document;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * file
 *
 * @author Tan
 * @version 1.0 2021/1/17
 */
@Data
@Builder
@Document("fs-file")
@AllArgsConstructor
@NoArgsConstructor
public class FileDocument {

    @Id
    private String id;

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

    @CreatedDate
    @ApiModelProperty("创建时间")
    private Date gmtCreated;

    @ApiModelProperty("预览地址")
    private String previewUrl;

}
