package com.sword.elucidator.domain.bo;

import com.sword.common.domain.PageBo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@ApiModel("文件搜索-Bo")
@EqualsAndHashCode(callSuper = true)
public class FileSearchBo extends PageBo {

    @ApiModelProperty("文件名称")
    private String name;

    @ApiModelProperty("文件类型")
    private String contentType;
}
