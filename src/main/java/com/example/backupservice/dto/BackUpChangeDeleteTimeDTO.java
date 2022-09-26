package com.example.backupservice.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class BackUpChangeDeleteTimeDTO {

    private List<String> documentId;
    private LocalDateTime deletedAt;
}
