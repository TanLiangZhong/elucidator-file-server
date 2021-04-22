package com.sword.elucidator.conf;

import com.mongodb.client.MongoClient;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * mongo 配置
 *
 * @author Tan
 * @version 1.0 2020/12/27
 */
@Configuration
public class MongoConf {

    @Bean
    public GridFSBucket gridFsBucket(MongoClient mongoClient, MongoProperties properties) {
        return GridFSBuckets.create(mongoClient.getDatabase(properties.getGridfs().getDatabase()), properties.getGridfs().getBucket());
    }

}
