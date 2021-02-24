package com.sword.elucidator.service.impl;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.sword.common.constant.BaseConstants;
import com.sword.elucidator.conf.FsProperties;
import com.sword.elucidator.domain.bo.UploadPartBo;
import com.sword.elucidator.domain.vo.FileUploadVo;
import com.sword.elucidator.exception.BusinessException;
import com.sword.elucidator.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonObjectId;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * 文件上传
 *
 * @author Tan
 * @version 1.0 2021/1/17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    private final FsProperties fsProperties;
    private final MongoProperties mongoProperties;

    private final GridFsTemplate gridFsTemplate;
    private final MongoClient mongoClient;

    @Override
    public FileUploadVo upload(MultipartFile file) {
        try {
            String fileName = Optional.ofNullable(file.getOriginalFilename()).orElse(file.getName());
            Document metadata = new Document()
                    .append(BaseConstants.FILE_METADATA_CONTENT_TYPE, file.getContentType())
                    .append(BaseConstants.FILE_METADATA_SUFFIX, fileName.substring(fileName.lastIndexOf(".")));
            log.info("name: {}, size: {}", fileName, file.getSize());
            ObjectId objectId = gridFsTemplate.store(file.getInputStream(), fileName, metadata);
            log.info("upload success {}, objectId: {}", fileName, objectId.toHexString());
            return FileUploadVo.builder()
                    .fileId(objectId.toHexString())
                    .name(fileName)
                    .size(file.getSize())
                    .contentType(metadata.getString(BaseConstants.FILE_METADATA_CONTENT_TYPE))
                    .suffix(metadata.getString(BaseConstants.FILE_METADATA_SUFFIX))
                    .previewUrl(fsProperties.getBasePreviewUrl() + "/preview/" + objectId.toHexString() + metadata.getString(BaseConstants.FILE_METADATA_SUFFIX))
                    .build();
        } catch (Exception e) {
            log.info("upload file exception <<<=== ", e);
            throw new BusinessException("File upload failed！Error: " + e.getMessage());
        }
    }

    @Override
    public List<FileUploadVo> batchUpload(MultipartFile[] file) {
        return Stream.of(file).map(this::upload).collect(Collectors.toList());
    }

    @Override
    public FileUploadVo uploadPart(UploadPartBo part) {
        log.info("uploadPart ===>>> part: {}, chunkSize: {}, ContentType: {}", part, part.getFile().getSize(), part.getFile().getContentType());
        try {
            MultipartFile file = part.getFile();
            if (StringUtils.hasText(part.getFileId())) {
                // TODO 块验证
                ObjectId objectId = new ObjectId(part.getFileId());
                writeChunk(new BsonObjectId(objectId), part.getChunkIndex(), file.getBytes());
                return FileUploadVo.builder().fileId(objectId.toHexString()).build();
            } else {
                ObjectId objectId = new ObjectId();
                BsonValue fileId = new BsonObjectId(objectId);

                String fileName = part.getName();
                Document metadata = new Document()
                        .append(BaseConstants.FILE_METADATA_CONTENT_TYPE, file.getContentType())
                        .append(BaseConstants.FILE_METADATA_SUFFIX, fileName.substring(fileName.lastIndexOf(".")));
                log.info(" --metadata--  {}", metadata);

                MongoCollection<GridFSFile> filesCollection = getFilesCollection(mongoClient.getDatabase(mongoProperties.getGridfs().getDatabase()), mongoProperties.getGridfs().getBucket());
                GridFSFile gridFSFile = new GridFSFile(fileId, fileName, part.getSize(), part.getChunkSize(), new Date(), metadata);
                filesCollection.insertOne(gridFSFile);
                writeChunk(fileId, part.getChunkIndex(), file.getBytes());

                return FileUploadVo.builder()
                        .fileId(objectId.toHexString())
                        .name(fileName)
                        .size(part.getSize())
                        .contentType(metadata.getString(BaseConstants.FILE_METADATA_CONTENT_TYPE))
                        .suffix(metadata.getString(BaseConstants.FILE_METADATA_SUFFIX))
                        .previewUrl(fsProperties.getBasePreviewUrl() + "/preview/" + objectId.toHexString() + metadata.getString(BaseConstants.FILE_METADATA_SUFFIX))
                        .build();
            }
        } catch (IOException e) {
            log.error("fragment upload exception <<<=== part: {}, msg: {}", part, e.getMessage());
            throw new BusinessException("Fragment upload failed！Error: " + e.getMessage());
        }
    }

    private void writeChunk(BsonValue fileId, Integer chunkIndex, byte[] bytes) {
        log.info("writeChunk ===>>> fileId: {} , chunkIndex: {} , length: {} ", fileId, chunkIndex, bytes.length);
        MongoCollection<Document> chunksCollection = getChunksCollection(mongoClient.getDatabase(mongoProperties.getGridfs().getDatabase()), mongoProperties.getGridfs().getBucket());
        chunksCollection.insertOne(new Document("files_id", fileId).append("n", chunkIndex).append("data", new Binary(bytes)));
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
