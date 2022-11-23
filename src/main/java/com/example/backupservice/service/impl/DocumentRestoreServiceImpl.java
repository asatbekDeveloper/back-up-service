package com.example.backupservice.service.impl;

import com.example.backupservice.dto.BackUpCreateDTO;
import com.example.backupservice.entity.DocumentRestore;
import com.example.backupservice.repository.DocumentRestoreRepository;
import com.example.backupservice.service.DocumentRestoreService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

@Service
public class DocumentRestoreServiceImpl implements DocumentRestoreService {

    private final DocumentRestoreRepository repository;

    @Autowired
    public DocumentRestoreServiceImpl(DocumentRestoreRepository repository) {
        this.repository = repository;
    }

    @Override
    public DocumentRestore save(BackUpCreateDTO dto) {
        
        Optional<DocumentRestore> optionalDocumentRestore = repository.findByDocumentNameAndContentTypeAndDocumentSizeAndAlfrescoRootPathAndDocumentVersion(
                dto.getFile().getOriginalFilename(),
                dto.getFile().getContentType(),
                dto.getFile().getSize(),
                dto.getAlfrescoRootPath(),
                dto.getDocumentVersion()
        );

        return optionalDocumentRestore.orElseGet(() -> getDocumentRestore(dto));

    }


    private DocumentRestore getDocumentRestore(BackUpCreateDTO dto) {
        String filePath = fileUploadToServer(dto.getFile(), dto.getAlfrescoRootPath(), dto.getDocumentId());

        DocumentRestore documentRestore = DocumentRestore.builder()
                .documentName(dto.getFile().getOriginalFilename())
                .documentSize(dto.getFile().getSize())
                .contentType(dto.getFile().getContentType())
                .filePath(filePath)
                .fileDescription(dto.getDocumentDescription())
                .alfrescoRootPath(dto.getAlfrescoRootPath())
                .algorithm(dto.getAlgorithm())
                .documentVersion(dto.getDocumentVersion())
                .build();


        return repository.save(documentRestore);
    }

    @SneakyThrows
    private String fileUploadToServer(MultipartFile file, String rootPath, String documentId) {


        String documentIdWithoutVersion = documentId.substring(0, documentId.indexOf(';'));
        String extension = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf('.') + 1);

        File directory = new File("C:/Users/MrAkmal/Desktop/back-up/" + rootPath + "/" + documentIdWithoutVersion);

        if (!directory.exists()) directory.mkdirs();

        file.transferTo(new File(directory.getAbsolutePath() + "/" + documentId + "." + extension));

        return directory.getAbsolutePath() + "/" + documentId + "." + extension;
    }


}
