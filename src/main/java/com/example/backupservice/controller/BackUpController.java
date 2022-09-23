package com.example.backupservice.controller;


import com.example.backupservice.dto.BackUpCreateDTO;
import com.example.backupservice.service.BackUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/back_up")
public class BackUpController {


    private final BackUpService service;


    @Autowired
    public BackUpController(BackUpService service) {
        this.service = service;
    }


//    @PostMapping
//    public ResponseEntity<Void> saveFile(@ModelAttribute BackUpCreateDTO dto){
//        System.out.println("dto = " + dto);
//
//        service.save(dto);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> saveFile2(@RequestBody MultipartFile file){
        System.out.println("dto = " + file.getOriginalFilename());
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
