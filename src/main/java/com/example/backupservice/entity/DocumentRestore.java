package com.example.backupservice.entity;


import lombok.*;

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

    private String alfrescoRootPath;

    private String algorithm;

    private String documentName;

    private String documentVersion;

    private Long documentSize;

    private String contentType;

}
