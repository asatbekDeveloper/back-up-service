package com.example.backupservice.controller;


import com.example.backupservice.dto.BackUpChangeDeleteTimeDTO;
import com.example.backupservice.dto.BackUpCreateDTO;
import com.example.backupservice.dto.RestoreTimeDTO;
import com.example.backupservice.dto.RestoreWithNameDTO;
import com.example.backupservice.service.BackUpService;
import com.example.backupservice.service.impl.BackUpServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@Tag(name = "1. BackUp Controller", description = "BackUp Related APIs")
@RequestMapping("/v1/back_up")
@CrossOrigin("*")
public class BackUpController {

    private final BackUpService backUpService;

    private final ObjectMapper mapper;

    @Autowired
    public BackUpController(BackUpServiceImpl backUpService, ObjectMapper mapper) {
        this.backUpService = backUpService;
        this.mapper = mapper;
    }


    @Tag(name = "BUBackUp001", description = "Upload File to BackUp server with BackUpCreateDTO and MultipartFile ")
    @Operation(summary = "API ID: BUBackUp001")
    @SneakyThrows
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> saveFile(@RequestPart MultipartFile file, HttpServletRequest request) {

        BackUpCreateDTO backUpCreateDTO = mapper.readValue(request.getParameter("dto"), BackUpCreateDTO.class);
        backUpCreateDTO.setFile(file);
        backUpService.save(backUpCreateDTO);

        return new ResponseEntity<>(HttpStatus.OK);
    }


    @Tag(name = "BUBackUp002", description = "Restore Files in given timeline with RestoreTimeDTO ")
    @Operation(summary = "API ID: BUBackUp002")
    @PostMapping("/restore")
    public ResponseEntity<Void> restoreFiles(@Valid @RequestBody RestoreTimeDTO dto) {
        return backUpService.restore(dto);
    }


    @Tag(name = "BUBackUp003", description = "Restore Files with RestoreWithNameDTO ")
    @Operation(summary = "API ID: BUBackUp003")
    @PostMapping("/restore-with-name")
    public ResponseEntity<Void> restoreWithName(@Valid @RequestBody RestoreWithNameDTO dto) {
        return backUpService.restoreWithName(dto);
    }


    @Tag(name = "BUBackUp004", description = "Restore Files with document id")
    @Operation(summary = "API ID: BUBackUp004")
    @PostMapping("/restore-with-documentId")
    public ResponseEntity<Void> restoreWithDocumentId(@RequestParam String documentId) {
        return backUpService.restoreWithDocumentId(documentId);
    }


    @Tag(name = "BUBackUp005", description = "Change BackUp entity deletedAt status if files not found with BackUpChangeDeleteTimeDTO")
    @Operation(summary = "API ID: BUBackUp005")
    @PostMapping("/delete")
    public ResponseEntity<Void> changeStatusDeletedAt(@Valid @RequestBody BackUpChangeDeleteTimeDTO dto) {
        return backUpService.changeStatusDeletedAt(dto);
    }


}
