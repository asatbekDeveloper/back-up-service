package com.example.backupservice.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class BackUpCreateDTO {

    private String documentId;

    private String documentVersion;

    private MultipartFile file;

    private String alfrescoRootPath;

    private String documentDescription;

    private String algorithm;


}
