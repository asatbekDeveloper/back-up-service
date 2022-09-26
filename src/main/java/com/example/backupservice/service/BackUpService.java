package com.example.backupservice.service;

import com.example.backupservice.config.EncryptionConfig;
import com.example.backupservice.dto.*;
import com.example.backupservice.entity.BackUp;
import com.example.backupservice.entity.DocumentRestore;
import com.example.backupservice.repository.BackUpRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BackUpService {

    private final BackUpRepository repository;

    private final DocumentRestoreService restoreService;

    private final EncryptionConfig encryptionConfig;

    @Autowired
    public BackUpService(BackUpRepository repository, DocumentRestoreService restoreService, EncryptionConfig encryptionConfig) {
        this.repository = repository;
        this.restoreService = restoreService;
        this.encryptionConfig = encryptionConfig;
    }

    public void save(BackUpCreateDTO dto) {

        DocumentRestore documentRestore = restoreService.save(dto);


        Optional<BackUp> optionalBackUp = repository.findByDocumentRestoreId(documentRestore);

        BackUp backUp;
        if (optionalBackUp.isEmpty()) {
            backUp = BackUp.builder()
                    .documentId(dto.getDocumentId())
                    .documentRestoreId(documentRestore)
                    .deletedAt(null)
                    .backUpCount(0)
                    .build();

        } else {

            backUp = optionalBackUp.get();
            backUp.setDeletedAt(null);
            backUp.setBackUpCount(backUp.getBackUpCount() + 1);
            backUp.setDocumentId(dto.getDocumentId());

        }
        repository.save(backUp);

    }


    @SneakyThrows
    public ResponseEntity<Void> restore(RestoreTimeDTO dto) {

        Optional<List<BackUp>> optionalList = repository.findAllByDeletedAtIsBetween(dto.getBegin(), dto.getEnd());

        RestTemplate restTemplate = new RestTemplate();
        String restoreURI = "http://localhost:9090/v1/file/restore";

        if (optionalList.isEmpty()) throw new RuntimeException("Files not found to restore");


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);


        List<RestoreDTO> restoreDTOS = new ArrayList<>();
        for (BackUp backUp : optionalList.get()) {

            String alfrescoRootPath = backUp.getDocumentRestoreId().getAlfrescoRootPath();
            String withoutUserId = alfrescoRootPath.substring(0, alfrescoRootPath.lastIndexOf("/"));

            String userId = alfrescoRootPath.substring(alfrescoRootPath.lastIndexOf("/") + 1);

            String commonId = withoutUserId.substring(withoutUserId.lastIndexOf("/") + 1);

            String withoutCommonId = withoutUserId.substring(0, withoutUserId.lastIndexOf("/"));

            String userType = withoutCommonId.substring(withoutCommonId.indexOf("/") + 1);

            String fileVersion = backUp.getDocumentRestoreId().getFilePath().substring(
                    backUp.getDocumentRestoreId().getFilePath().lastIndexOf(';') + 1,
                    backUp.getDocumentRestoreId().getFilePath().lastIndexOf('.'));

            String documentId = backUp.getDocumentId().substring(0, backUp.getDocumentId().lastIndexOf(';'));


            try {
                FileInputStream reader = new FileInputStream(backUp.getDocumentRestoreId().getFilePath());
                RestoreDTO restoreDTO = new RestoreDTO(

                        new FileDTO(
                                backUp.getDocumentRestoreId().getDocumentName(),
                                backUp.getDocumentRestoreId().getDocumentSize().toString(),
                                backUp.getDocumentRestoreId().getContentType(),
                                getDecryptedContentBytes(reader.readAllBytes(), backUp.getDocumentRestoreId().getAlgorithm()),
                                fileVersion,
                                documentId
                        )
                        ,
                        backUp.getDocumentRestoreId().getFileDescription(),
                        Long.valueOf(userId),
                        Long.valueOf(commonId),
                        userType,
                        backUp.getDocumentRestoreId().getAlgorithm());


                restoreDTOS.add(restoreDTO);
                reader.close();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }

        }


        HttpEntity<List<RestoreDTO>> entity = new HttpEntity<>(restoreDTOS, headers);


        ResponseEntity<Void> response = restTemplate.exchange(restoreURI, HttpMethod.POST, entity, Void.class);

        System.out.println("response.getStatusCode() = " + response.getStatusCode());

        return null;
    }

    @SneakyThrows
    public byte[] getDecryptedContentBytes(byte[] encryptedContent, String encryptionAlgorithm) {

        byte[] decryptedContent = null;
        if (encryptionAlgorithm.equals("AES")) {
            SecretKey secretKey = new SecretKeySpec(encryptionConfig.aesSecretKey.getBytes(), encryptionAlgorithm);

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            decryptedContent = cipher.doFinal(encryptedContent);

        } else if (encryptionAlgorithm.equals("TripleDES")) {
            SecretKey secretKey = new SecretKeySpec(encryptionConfig.tdesSecretKey.getBytes(), encryptionAlgorithm);

            Cipher cipher = Cipher.getInstance("TripleDES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            decryptedContent = cipher.doFinal(encryptedContent);

        } else {
            decryptedContent = encryptedContent;
        }

        return decryptedContent;
    }


    public ResponseEntity<Void> changeStatusDeletedAt(BackUpChangeDeleteTimeDTO dto) {

        List<BackUp> optionalBackUpDocument = repository.findAllByDocumentIdIn(dto.getDocumentId());

        if (optionalBackUpDocument.isEmpty()) {
            throw new RuntimeException("File not found with " + dto.getDocumentId());
        }

        List<BackUp> backUps = optionalBackUpDocument
                .stream()
                .peek(backUp -> backUp.setDeletedAt(LocalDateTime.now())).toList();

        repository.saveAll(backUps);
        
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
