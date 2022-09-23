package com.example.backupservice.repository;

import com.example.backupservice.entity.DocumentRestore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRestoreRepository extends JpaRepository<DocumentRestore,Long> {
}
