package com.example.backupservice.service;

import com.example.backupservice.dto.BackUpCreateDTO;
import com.example.backupservice.entity.DocumentRestore;
import lombok.SneakyThrows;

public interface DocumentRestoreService {


    @SneakyThrows
    DocumentRestore save(BackUpCreateDTO dto);




}
