package com.sword.elucidator.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Fs  配置
 *
 * @author Tan
 * @version 1.0 2020-11-02
 */
@Data
@Component
@ConfigurationProperties("base.fs")
public class FsProperties {

    private String basePreviewUrl;

}