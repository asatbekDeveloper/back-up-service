package com.example.backupservice.repository;

import com.example.backupservice.entity.DocumentRestore;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.print.Doc;
import java.util.Optional;

public interface DocumentRestoreRepository extends JpaRepository<DocumentRestore,Long> {

    Optional<DocumentRestore> findByDocumentNameAndContentTypeAndDocumentSizeAndAlfrescoRootPathAndDocumentVersion(
            String documentName,
            String contentType,
            Long documentSize,
            String alfrescoRootPath,
            String documentVersion);
}
