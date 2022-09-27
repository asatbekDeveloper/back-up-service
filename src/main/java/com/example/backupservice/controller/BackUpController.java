package com.example.backupservice.controller;


import com.example.backupservice.dto.BackUpChangeDeleteTimeDTO;
import com.example.backupservice.dto.BackUpCreateDTO;
import com.example.backupservice.dto.RestoreTimeDTO;
import com.example.backupservice.service.BackUpService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.Month;

@RestController
@RequestMapping("/v1/back_up")
public class BackUpController {


    private final BackUpService service;

    private final ObjectMapper mapper;


    @Autowired
    public BackUpController(BackUpService service, ObjectMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }


    @SneakyThrows
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> saveFile(@RequestPart MultipartFile file, HttpServletRequest request){

        BackUpCreateDTO backUpCreateDTO = mapper.readValue(request.getParameter("dto"), BackUpCreateDTO.class);
        backUpCreateDTO.setFile(file);
        service.save(backUpCreateDTO);

        return new ResponseEntity<>(HttpStatus.OK);
    }



    @PostMapping("/restore")
    public ResponseEntity<Void> restoreFiles(){
        RestoreTimeDTO some = new RestoreTimeDTO(LocalDateTime.of(2022, Month.SEPTEMBER, 25,
                10, 10, 10), LocalDateTime.now(), "some");
        return service.restore(some);
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> changeStatusDeletedAt(@RequestBody BackUpChangeDeleteTimeDTO dto){
        return service.changeStatusDeletedAt(dto);
    }


}
