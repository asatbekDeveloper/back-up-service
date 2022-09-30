package com.example.backupservice.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestoreWithNameDTO {


    private String documentType;

    private Long userId;

    private Long commonId;

    private String documentName;


}
