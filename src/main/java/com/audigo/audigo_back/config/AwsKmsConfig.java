package com.audigo.audigo_back.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;

/**
 * AWS KMS 설정
 */
@Configuration
@ConfigurationProperties(prefix = "aws.kms")
@Getter
@Setter
public class AwsKmsConfig {

    private String region;
    private Key key;
    private Eoa eoa;
    private Access access;
    private Secret secret;

    @Getter
    @Setter
    public static class Key {
        private String id;
    }

    @Getter
    @Setter
    public static class Eoa {
        private Key key;
    }

    @Getter
    @Setter
    public static class Access {
        private Key key;
    }

    @Getter
    @Setter
    public static class Secret {
        private Access access;
        private Key key;
    }

    /**
     * KMS 클라이언트 빈 생성
     */
    @Bean
    public KmsClient kmsClient() {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(
                access.getKey().getId(),
                secret.getKey().getId()
        );

        return KmsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }
}
