package com.sword.elucidator.domain.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * fs-file
 *
 * @author Tan
 * @version 1.0 2020/12/26
 */
@Data
@Document("fs-file")
@AllArgsConstructor
@NoArgsConstructor
public class FsFileDocument {

    @Id
    private String id;

    /**
     * 文件名称
     */
    private String name;

    /**
     * 文件类型
     */
    private String contentType;

    /**
     * 文件大小
     */
    private Long size;

    /**
     * 文件后缀
     */
    private String suffix;

    /**
     * MD5 值
     */
    private String md5;

    /**
     * 创建时间
     */
    private LocalDateTime gmtCreated;

    /**
     * 文件内容Id
     */
    private String contentId;

    public FsFileDocument(String name, String contentType, Long size, String suffix, String md5, LocalDateTime gmtCreated, String contentId) {
        this.name = name;
        this.contentType = contentType;
        this.size = size;
        this.suffix = suffix;
        this.md5 = md5;
        this.gmtCreated = gmtCreated;
        this.contentId = contentId;
    }

}
