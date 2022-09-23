package com.example.backupservice.entity;


import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;

@AllArgsConstructor

@NoArgsConstructor
@Setter
@Getter
@Builder
@Entity
@Table(name = "document_restore")
public class DocumentRestore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filePath;

    private String fileDescription;

    private Long userId;

    private Long commonId;

    private String userType;

    private String algorithm;

}
