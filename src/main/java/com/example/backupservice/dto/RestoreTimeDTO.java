package com.example.backupservice.dto;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RestoreTimeDTO {


    private LocalDateTime begin;

    private LocalDateTime end;

    private String userType; // amendment or dispute

}
