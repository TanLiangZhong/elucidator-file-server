package com.sword.elucidator.domain.document;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.Document;

/**
 * 文件元数据
 *
 * @author Tan
 * @version 1.0 2021/1/16
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FileMetadata extends Document {

    /**
     * Optional. A valid MIME type for the GridFS file. For application use only.
     */
    private String contentType;

    /**
     * Optional. file suffix
     */
    private String suffix;

}
