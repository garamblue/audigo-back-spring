package com.audigo.audigo_back.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;

/**
 * Web3 및 AWS KMS 설정
 */
@Configuration
@ConfigurationProperties(prefix = "web3")
@Getter
@Setter
public class Web3Config {

    private Rpc rpc;
    private Chain chain;
    private Token token;
    private Main main;

    @Getter
    @Setter
    public static class Rpc {
        private String url;
    }

    @Getter
    @Setter
    public static class Chain {
        private long id;
    }

    @Getter
    @Setter
    public static class Token {
        private String address;
        private Manager manager;
    }

    @Getter
    @Setter
    public static class Manager {
        private String address;
    }

    @Getter
    @Setter
    public static class Main {
        private Wallet wallet;
    }

    @Getter
    @Setter
    public static class Wallet {
        private String address;
    }

    /**
     * Web3j 클라이언트 빈 생성
     */
    @Bean
    public Web3j web3j() {
        return Web3j.build(new HttpService(rpc.getUrl()));
    }
}
