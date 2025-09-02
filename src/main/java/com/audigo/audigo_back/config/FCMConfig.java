package com.audigo.audigo_back.config;

import java.io.InputStream;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import io.jsonwebtoken.io.IOException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class FCMConfig {
    private static final String FIREBASE_CONFIG_PATH = "fcm_auth.json";

    @PostConstruct
    public void initialize() {
        try {
            // resources 폴더에서 서비스 계정 키 파일 로드
            InputStream serviceAccount = new ClassPathResource(FIREBASE_CONFIG_PATH).getInputStream();

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

            // FirebaseApp이 이미 초기화되었는지 확인
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException | java.io.IOException e) {
            e.printStackTrace();
            log.info("=== FCMConfig initialize IOException: " + e.toString());
        }
    }
}
