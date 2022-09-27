package com.example.backupservice.service;

import com.example.backupservice.dto.BackUpCreateDTO;
import com.example.backupservice.entity.DocumentRestore;
import com.example.backupservice.repository.DocumentRestoreRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

@Service
public class DocumentRestoreService {

    private final DocumentRestoreRepository repository;

    @Autowired
    public DocumentRestoreService(DocumentRestoreRepository repository) {
        this.repository = repository;
    }

    @SneakyThrows
    public DocumentRestore save(BackUpCreateDTO dto) {

        System.out.println("dto = " + dto);

//        String version = dto.getDocumentId().substring(dto.getDocumentId().indexOf(';') + 1);

        Optional<DocumentRestore> optionalDocumentRestore = repository.findByDocumentNameAndContentTypeAndDocumentSizeAndAlfrescoRootPathAndDocumentVersion(
                dto.getFile().getOriginalFilename(),
                dto.getFile().getContentType(),
                dto.getFile().getSize(),
                dto.getAlfrescoRootPath(),
                dto.getDocumentVersion()
        );

//        System.out.println("optionalDocumentRestore.get() = " + optionalDocumentRestore.get());

        if (optionalDocumentRestore.isPresent()) {

            return optionalDocumentRestore.get();
//            InputStream inputStream = dto.getFile().getInputStream();
//
//            String filePath1 = optionalDocumentRestore.get().getFilePath();
//            File file = new File(filePath1);
//
//            try (InputStream inputStream1 = new FileInputStream(file)) {
//
//                if (inputStream1.equals(inputStream)) return optionalDocumentRestore.get();
//
//            } catch (Exception e) {
//                throw new RuntimeException(e.getMessage());
//            }


        }
        return getDocumentRestore(dto);


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

        File directory = new File("C:/Users/Asus/Desktop/back-up/" + rootPath + "/" + documentIdWithoutVersion);

        if (!directory.exists()) directory.mkdirs();

        file.transferTo(new File(directory.getAbsolutePath() + "/" + documentId + "." + extension));

        return directory.getAbsolutePath() + "/" + documentId + "." + extension;
    }


}
