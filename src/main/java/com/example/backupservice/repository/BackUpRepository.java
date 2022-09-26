package com.example.backupservice.repository;

import com.example.backupservice.entity.BackUp;
import com.example.backupservice.entity.DocumentRestore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BackUpRepository extends JpaRepository<BackUp,Long> {


    Optional<BackUp>  findByDocumentRestoreId(DocumentRestore documentRestoreId);



    Optional<List<BackUp>> findAllByDeletedAtIsBetween(LocalDateTime begin, LocalDateTime end);

    Optional<BackUp> findByDocumentId(String documentId);

    List<BackUp> findAllByDocumentIdIn(List<String> documentId);
}
