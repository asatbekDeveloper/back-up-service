package com.example.backupservice.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class DocumentRepositoryDeleteDTO {


    private Long commonId;

    private String documentDescription;

    private String documentName;

    private String uploadedBy;

    private Long documentSize;

    private String userType;

    private String documentId;

}
