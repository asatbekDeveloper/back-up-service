package com.example.backupservice.service.impl;

import com.example.backupservice.config.AlfrescoConfig;
import com.example.backupservice.config.EncryptionConfig;
import com.example.backupservice.dto.*;
import com.example.backupservice.entity.BackUp;
import com.example.backupservice.entity.DocumentRestore;
import com.example.backupservice.repository.BackUpRepository;
import com.example.backupservice.service.AlfrescoService;
import com.example.backupservice.service.BackUpService;
import com.example.backupservice.service.DocumentRestoreService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
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
public class BackUpServiceImpl implements BackUpService {

    private final BackUpRepository repository;

    private final DocumentRestoreService restoreService;

    private final EncryptionConfig encryptionConfig;

    private final AlfrescoService alfrescoService;

    private final RestTemplate restTemplate;

    private final AlfrescoConfig alfrescoConfig;


    @Autowired
    public BackUpServiceImpl(BackUpRepository repository, DocumentRestoreService restoreService, EncryptionConfig encryptionConfig, AlfrescoService alfrescoService, RestTemplate restTemplate, AlfrescoConfig alfrescoConfig) {
        this.repository = repository;
        this.restoreService = restoreService;
        this.encryptionConfig = encryptionConfig;
        this.alfrescoService = alfrescoService;
        this.restTemplate = restTemplate;
        this.alfrescoConfig = alfrescoConfig;
    }

    @Override
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


    @Override
    public ResponseEntity<Void> restoreWithName(RestoreWithNameDTO dto) {

        String alfrescoRootPath = "%s/%s/%d/%d".formatted(alfrescoConfig.baseFolder, dto.getDocumentType(), dto.getCommonId(), dto.getUserId());
        BackUp backUp = repository.findAllByDocumentName(alfrescoRootPath, dto.getDocumentName());

        String documentVersion = backUp.getDocumentRestoreId().getDocumentVersion();

        String documentIdWithoutVersion = backUp.getDocumentId().substring(0, backUp.getDocumentId().indexOf(";"));

        List<BackUp> backUps = new ArrayList<>();
        if (!documentVersion.equals("1.0")) {
            backUps = repository.findAllByDocumentIdWithoutVersion(documentIdWithoutVersion);
        } else {
            backUps.add(backUp);
        }

        callWithRestTemplateToUploadServer(backUps);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @Override
    public ResponseEntity<Void> restoreWithDocumentId(String documentId) {

        documentId = documentId.substring(0, documentId.indexOf(";"));

        List<BackUp> backUps = repository.findByDocumentIdStartsWithAndDeletedAtIsNotNull(documentId);

        callWithRestTemplateToUploadServer(backUps);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



    @Override
    public ResponseEntity<Void> restore(RestoreTimeDTO dto) {

        List<BackUp> backUpList = repository.findAllByDeletedAtIsBetween(dto.getBegin(), dto.getEnd());


        if (backUpList.isEmpty()) throw new RuntimeException("Files not found to restore");

        callWithRestTemplateToUploadServer(backUpList);

        return null;
    }

    private ResponseEntity<Void> callWithRestTemplateToUploadServer(List<BackUp> optionalList) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);


        List<RestoreDTO> restoreDTOS = new ArrayList<>();
        for (BackUp backUp : optionalList) {

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

        String restoreURI = "http://localhost:9090/v1/file/restore";
        return restTemplate.exchange(restoreURI, HttpMethod.POST, entity, Void.class);
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


    @Override
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


    @Scheduled(cron = "0/40 * * * * ?")
    public void watchAlfrescoFileExist() {
        List<BackUp> backUps = repository.findAll();

        List<BackUp> backUpsChanged = backUps.stream()
                .filter(backUp -> backUp.getDeletedAt() == null)
                .filter(backUp -> alfrescoService.findByDocumentId(backUp.getDocumentId()) == null)
                .peek(backUp -> backUp.setDeletedAt(LocalDateTime.now()))
                .toList();

        repository.saveAll(backUpsChanged);


        List<DocumentRepositoryDeleteDTO> docs = backUpsChanged.stream().map(backUp -> {

            String alfrescoRootPath = backUp.getDocumentRestoreId().getAlfrescoRootPath();
            String withoutUserId = alfrescoRootPath.substring(0, alfrescoRootPath.lastIndexOf("/"));

            String userId = alfrescoRootPath.substring(alfrescoRootPath.lastIndexOf("/") + 1);

            String commonId = withoutUserId.substring(withoutUserId.lastIndexOf("/") + 1);

            String withoutCommonId = withoutUserId.substring(0, withoutUserId.lastIndexOf("/"));

            String userType = withoutCommonId.substring(withoutCommonId.indexOf("/") + 1);

            return new DocumentRepositoryDeleteDTO(Long.valueOf(commonId), backUp.getDocumentRestoreId().getFileDescription(), backUp.getDocumentRestoreId().getDocumentName(), userId, backUp.getDocumentRestoreId().getDocumentSize(), userType, backUp.getDocumentId());
        }).toList();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String reposDeleteURI = "http://localhost:9090/v1/file/delete_repo";

        HttpEntity<List<DocumentRepositoryDeleteDTO>> entity = new HttpEntity<>(docs, headers);
        restTemplate.exchange(reposDeleteURI, HttpMethod.POST, entity, Void.class);

    }


}
