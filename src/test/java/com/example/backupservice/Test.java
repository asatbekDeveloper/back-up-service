package com.example.backupservice;

import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;

public class Test {


    @SneakyThrows
    public static void main(String[] args) {
        File file = new File("C:\\Users\\Asus\\Desktop\\Books/JUnit+5+migration+guide.png");

        File directory = new File("C:/Users/Asus/Desktop/back-up/" + "DisputeDocuments/1/2");

        if (!directory.exists()) directory.mkdirs();

        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file",
                file.getName(), "application/pdf", IOUtils.toByteArray(input));

        String extension = FilenameUtils.getExtension(file.getName());
        String baseName = FilenameUtils.getBaseName(file.getName());
        multipartFile.transferTo(new File(directory.getAbsolutePath() + "/" + baseName+"-1.1."+extension));

        String s = directory.getAbsolutePath() + "/" + multipartFile.getOriginalFilename();
        System.out.println("s = " + s);

    }

}
