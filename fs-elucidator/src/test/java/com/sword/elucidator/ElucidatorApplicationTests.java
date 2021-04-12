package com.sword.elucidator;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoGridFSException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSUploadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.sword.common.constant.BaseConstants;
import com.sword.elucidator.domain.document.FileMetadata;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.BsonObjectId;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@SpringBootTest
class ElucidatorApplicationTests {

    Log log = LogFactory.getLog(ElucidatorApplicationTests.class);

    @Resource
    private MongoClient mongoClient;
    @Resource
    private MongoProperties properties;

//    @Test
//    void contextLoads() throws IOException {
//        File file = new File("C:\\Users\\liang\\Pictures\\壁纸_1\\lou-ll-maid-58-small.jpg");
//        FileInputStream inputStream = new FileInputStream(file);
//        MockMultipartFile file1 = new MockMultipartFile(file.getName(), inputStream);
//        upload(file1);
//    }

    public void upload(MultipartFile file) throws IOException {

        MongoCollection<Document> chunksCollection = getChunksCollection(mongoClient.getDatabase(properties.getGridfs().getDatabase()), properties.getGridfs().getBucket());
        MongoCollection<GridFSFile> filesCollection = getFilesCollection(mongoClient.getDatabase(properties.getGridfs().getDatabase()), properties.getGridfs().getBucket());

        log.info(filesCollection.countDocuments());
        log.info(chunksCollection.countDocuments());

        ObjectId id = new ObjectId();
        BsonValue fileId = new BsonObjectId(id);

        Document metadata = new Document();
        String fileName = file.getName();
        metadata.append(BaseConstants.FILE_METADATA_CONTENT_TYPE, file.getContentType())
                .append(BaseConstants.FILE_METADATA_SUFFIX, fileName.substring(fileName.lastIndexOf(".")));

        GridFSFile gridFSFile = new GridFSFile(fileId, fileName, file.getSize(), BaseConstants.DEFAULT_CHUNK_SIZE_BYTES, new Date(),
                metadata);

        filesCollection.insertOne(gridFSFile);

        InputStream source = file.getInputStream();

        byte[] buffer = new byte[BaseConstants.DEFAULT_CHUNK_SIZE_BYTES];
        int len;
        int chunkIndex = 0;
        try {
            while ((len = source.read(buffer)) != -1) {
                System.out.println("len : " + len);
                byte[] newBuffer = new byte[len];
                System.arraycopy(buffer, 0, newBuffer, 0, len);
                chunksCollection.insertOne(new Document("files_id", fileId).append("n", chunkIndex).append("data", new Binary(newBuffer)));
                chunkIndex++;
            }
            source.close();
        } catch (IOException e) {
            chunksCollection.deleteMany(new Document("files_id", fileId));
            throw new MongoGridFSException("IOException when reading from the InputStream", e);
        }

    }

    private static MongoCollection<GridFSFile> getFilesCollection(final MongoDatabase database, final String bucketName) {
        return database.getCollection(bucketName + ".files", GridFSFile.class).withCodecRegistry(
                fromRegistries(database.getCodecRegistry(), MongoClientSettings.getDefaultCodecRegistry())
        );
    }

    private static MongoCollection<Document> getChunksCollection(final MongoDatabase database, final String bucketName) {
        return database.getCollection(bucketName + ".chunks").withCodecRegistry(MongoClientSettings.getDefaultCodecRegistry());
    }


}
