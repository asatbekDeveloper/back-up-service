package com.example.backupservice.config;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.security.KeyPair;
import java.security.KeyPairGenerator;


@Component
public class EncryptionConfig {

    @Value("${aes.secret.key}")
    public String aesSecretKey;

    @Value("${tdes.secret.key}")
    public String tdesSecretKey;


    @Bean
    @SneakyThrows
    public KeyPair getKeyPairRSA() {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");

        generator.initialize(2048);

        return generator.genKeyPair();
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

}
