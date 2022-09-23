package com.example.backupservice.service;

import com.example.backupservice.dto.BackUpCreateDTO;
import com.example.backupservice.entity.DocumentRestore;
import com.example.backupservice.repository.DocumentRestoreRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Optional;

@Service
public class DocumentRestoreService {

    private final DocumentRestoreRepository repository;

    @Autowired
    public DocumentRestoreService(DocumentRestoreRepository repository) {
        this.repository = repository;
    }

    public DocumentRestore save(BackUpCreateDTO dto) {


        Optional<DocumentRestore> optionalDocumentRestore = repository.findByDocumentNameAndContentTypeAndDocumentSizeAndAlfrescoRootPathAndDocumentVersion(
                dto.getFile().getOriginalFilename(),
                dto.getFile().getContentType(),
                dto.getFile().getSize(),
                dto.getAlfrescoRootPath(),
                dto.getDocumentVersion()
        );

        if (optionalDocumentRestore.isEmpty()) {

            String filePath = fileUploadToServer(dto.getFile(), dto.getAlfrescoRootPath());

            DocumentRestore documentRestore = DocumentRestore.builder()
                    .documentName(dto.getFile().getOriginalFilename())
                    .documentSize(dto.getFile().getSize())
                    .contentType(dto.getFile().getContentType())
                    .filePath(filePath)
                    .fileDescription(dto.getDocumentDescription())
                    .alfrescoRootPath(dto.getAlfrescoRootPath())
                    .algorithm(dto.getAlgorithm())
                    .build();


            return repository.save(documentRestore);

        }

        return optionalDocumentRestore.get();

    }

    @SneakyThrows
    private String fileUploadToServer(MultipartFile file, String rootPath) {

        File directory = new File("C:/Users/Asus/Desktop/back-up/" + rootPath);

        if (!directory.exists()) directory.mkdirs();

        file.transferTo(new File(directory.getAbsolutePath() + "/" + file.getOriginalFilename()));

        return directory.getAbsolutePath() + "/" + file.getOriginalFilename();
    }


}
