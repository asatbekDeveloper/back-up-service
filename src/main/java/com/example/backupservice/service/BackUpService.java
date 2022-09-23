package com.example.backupservice.service;

import com.example.backupservice.dto.BackUpCreateDTO;
import com.example.backupservice.entity.BackUp;
import com.example.backupservice.entity.DocumentRestore;
import com.example.backupservice.repository.BackUpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BackUpService {

    private final BackUpRepository repository;

    private final DocumentRestoreService restoreService;

    @Autowired
    public BackUpService(BackUpRepository repository, DocumentRestoreService restoreService) {
        this.repository = repository;
        this.restoreService = restoreService;
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
            backUp.setDocumentId(dto.getDocumentId());

        }
        repository.save(backUp);

    }


}
