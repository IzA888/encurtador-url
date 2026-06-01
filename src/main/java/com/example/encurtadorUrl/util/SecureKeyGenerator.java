package com.example.encurtadorUrl.util;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component
public class SecureKeyGenerator {

    private static final String CHAR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Integer KEY_LENGTH = 6;
    private final SecureRandom random = new SecureRandom();

    public String genRandomKey() {
        StringBuilder sb = new StringBuilder(KEY_LENGTH);
        for ( int i = 0; i < KEY_LENGTH; i++){
            int randomIndex = random.nextInt(CHAR.length());
            sb.append(CHAR.charAt(randomIndex));
        }
        return sb.toString();
    }

}