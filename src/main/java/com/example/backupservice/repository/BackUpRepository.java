package com.example.backupservice.repository;

import com.example.backupservice.entity.BackUp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BackUpRepository extends JpaRepository<BackUp,Long> {
}
