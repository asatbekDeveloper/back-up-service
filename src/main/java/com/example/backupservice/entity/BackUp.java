package com.example.backupservice.entity;


import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
@Entity
@Table(name = "back_up")
public class BackUp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String documentId;

    private LocalDateTime deletedAt;

    private int backUpCount;

    @OneToOne
    private DocumentRestore documentRestoreId;


}
