package com.example.backupservice.service;

import com.example.backupservice.dto.BackUpChangeDeleteTimeDTO;
import com.example.backupservice.dto.BackUpCreateDTO;
import com.example.backupservice.dto.RestoreTimeDTO;
import com.example.backupservice.dto.RestoreWithNameDTO;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;

public interface BackUpService {


    void save(BackUpCreateDTO dto);


    ResponseEntity<Void> restoreWithName(RestoreWithNameDTO dto);

    ResponseEntity<Void> restoreWithDocumentId(String documentId);

    @SneakyThrows
    ResponseEntity<Void> restore(RestoreTimeDTO dto);

    ResponseEntity<Void> changeStatusDeletedAt(BackUpChangeDeleteTimeDTO dto);

}
