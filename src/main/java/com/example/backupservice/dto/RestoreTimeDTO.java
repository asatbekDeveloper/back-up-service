package com.example.backupservice.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RestoreTimeDTO {


    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDateTime begin;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDateTime end;

    private String userType; // amendment or dispute

}
