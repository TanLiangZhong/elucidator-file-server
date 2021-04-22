package com.sword.common.constant;

/**
 * 基础常量
 *
 * @author Tan
 * @version 1.0 2020/12/27
 */
public interface BaseConstants {

    /**
     * 小文件最大大小
     */
    Integer DEFAULT_CHUNK_SIZE_BYTES = 255 * 1024;

    /**
     * 文件元数据
     */
    String FILE_METADATA_CONTENT_TYPE = "_contentType";
    String FILE_METADATA_SUFFIX = "_suffix";
}
