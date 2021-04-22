package com.sword.elucidator.service.impl;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.sword.common.domain.PageVo;
import com.sword.elucidator.conf.FsProperties;
import com.sword.elucidator.domain.bo.FileSearchBo;
import com.sword.elucidator.domain.bo.UploadPartBo;
import com.sword.elucidator.domain.document.FileDocument;
import com.sword.elucidator.domain.document.FileMetadata;
import com.sword.elucidator.exception.BusinessException;
import com.sword.elucidator.repository.FileRepository;
import com.sword.elucidator.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonObjectId;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.data.domain.*;
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

    private final FileRepository fileRepository;

    @Override
    public FileDocument upload(MultipartFile file) {
        try {
            String fileName = Optional.ofNullable(file.getOriginalFilename()).orElse(file.getName());

            FileMetadata metadata = new FileMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setSuffix(fileName.substring(fileName.lastIndexOf(".")));
            log.info("name: {}, size: {}", fileName, file.getSize());
            ObjectId objectId = gridFsTemplate.store(file.getInputStream(), fileName, metadata);
            log.info("upload success {}, objectId: {}", fileName, objectId.toHexString());

            FileDocument fileDocument = FileDocument.builder()
                    .fileId(objectId.toHexString())
                    .name(fileName)
                    .size(file.getSize())
                    .contentType(metadata.getContentType())
                    .suffix(metadata.getSuffix())
                    .previewUrl(fsProperties.getBasePreviewUrl() + "/preview/" + objectId.toHexString() + metadata.getSuffix())
                    .build();
            fileRepository.save(fileDocument);
            return fileDocument;
        } catch (Exception e) {
            log.info("upload file exception <<<=== ", e);
            throw new BusinessException("File upload failed！Error: " + e.getMessage());
        }
    }

    @Override
    public List<FileDocument> batchUpload(MultipartFile[] file) {
        return Stream.of(file).map(this::upload).collect(Collectors.toList());
    }

    @Override
    public FileDocument uploadPart(UploadPartBo part) {
        log.info("uploadPart ===>>> part: {}, chunkSize: {}, ContentType: {}", part, part.getFile().getSize(), part.getFile().getContentType());
        try {
            MultipartFile file = part.getFile();
            if (StringUtils.hasText(part.getFileId())) {
                // TODO 块验证
                ObjectId objectId = new ObjectId(part.getFileId());
                writeChunk(new BsonObjectId(objectId), part.getChunkIndex(), file.getBytes());
                return FileDocument.builder().fileId(objectId.toHexString()).build();
            } else {
                ObjectId objectId = new ObjectId();
                BsonValue fileId = new BsonObjectId(objectId);

                String fileName = part.getName();
                FileMetadata metadata = new FileMetadata();
                metadata.setContentType(file.getContentType());
                metadata.setSuffix(fileName.substring(fileName.lastIndexOf(".")));
                log.info(" --metadata--  {}", metadata);

                MongoCollection<GridFSFile> filesCollection = getFilesCollection(mongoClient.getDatabase(mongoProperties.getGridfs().getDatabase()), mongoProperties.getGridfs().getBucket());
                GridFSFile gridFSFile = new GridFSFile(fileId, fileName, part.getSize(), part.getChunkSize(), new Date(), metadata);
                filesCollection.insertOne(gridFSFile);
                writeChunk(fileId, part.getChunkIndex(), file.getBytes());

                FileDocument fileDocument = FileDocument.builder()
                        .fileId(objectId.toHexString())
                        .name(fileName)
                        .size(file.getSize())
                        .contentType(metadata.getContentType())
                        .suffix(metadata.getSuffix())
                        .previewUrl(fsProperties.getBasePreviewUrl() + "/preview/" + objectId.toHexString() + metadata.getSuffix())
                        .build();
                fileRepository.save(fileDocument);
                return fileDocument;
            }
        } catch (IOException e) {
            log.error("fragment upload exception <<<=== part: {}, msg: {}", part, e.getMessage());
            throw new BusinessException("Fragment upload failed！Error: " + e.getMessage());
        }
    }

    @Override
    public PageVo<FileDocument> findPage(FileSearchBo bo) {
        Pageable pageable = PageRequest.of(bo.getCurrent() - 1, bo.getSize(), Sort.by(Sort.Order.desc("gmtCreated")));
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //改变默认字符串匹配方式：模糊查询
                .withIgnoreCase(true) //改变默认大小写忽略方式：忽略大小写
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains()) //采用“包含匹配”的方式查询
                .withMatcher("contentType", ExampleMatcher.GenericPropertyMatchers.startsWith()) // 前缀匹配
                ;
        Example<FileDocument> example = Example.of(FileDocument.builder()
                .name(bo.getName())
                .contentType(bo.getContentType())
                .build(), exampleMatcher);

        Page<FileDocument> page = fileRepository.findAll(example, pageable);
        return new PageVo<>(bo.getCurrent(), bo.getSize(), page.getTotalPages(), page.getTotalElements(), page.getContent());
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
