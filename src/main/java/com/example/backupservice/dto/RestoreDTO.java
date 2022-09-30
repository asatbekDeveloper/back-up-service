package com.example.backupservice.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class RestoreDTO {


    private FileDTO fileDTO;

    private String fileDescription;

    private Long userId;

    private Long commonId;//maybe amendment or dispute

    private String documentType;

    private String algorithm;

}
