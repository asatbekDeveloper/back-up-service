package com.example.backupservice.repository;

import com.example.backupservice.entity.BackUp;
import com.example.backupservice.entity.DocumentRestore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BackUpRepository extends JpaRepository<BackUp,Long> {


    Optional<BackUp>  findByDocumentRestoreId(DocumentRestore documentRestoreId);


}
