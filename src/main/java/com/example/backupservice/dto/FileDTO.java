package com.example.backupservice.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileDTO {


    private  String fileOriginalName;

    private  String size;

    private  String contentType;

    private  byte[] content;

    private String fileVersion;

    private String documentId;

}
