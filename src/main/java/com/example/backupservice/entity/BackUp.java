package com.example.backupservice.entity;


import com.sun.xml.internal.ws.api.server.SDDocument;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Entity
@Table(name = "back_up")
public class BackUp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String documentId;

    private String documentVersion;

    private String alfrescoRootPath;

    private LocalDateTime deletedAt;

    private int backUpCount;

    @OneToOne
    private DocumentRestore documentRestoreId;


}
