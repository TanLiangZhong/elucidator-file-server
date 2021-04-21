package com.sword.elucidator.repository;

import com.sword.elucidator.domain.document.FileDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * fs-file
 *
 * @author Tan
 * @version 1.0 2020/12/26
 */
@Repository
public interface FileRepository extends MongoRepository<FileDocument, String> {
}
