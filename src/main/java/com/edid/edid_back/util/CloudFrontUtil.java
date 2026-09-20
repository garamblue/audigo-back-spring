package com.audigo.audigo_back.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.cloudfront.CloudFrontUtilities;
import software.amazon.awssdk.services.cloudfront.model.CannedSignerRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * AWS CloudFront Signed URL 생성 유틸리티
 */
@Component
@Slf4j
public class CloudFrontUtil {

    @Value("${aws.cloudfront.domain}")
    private String cloudFrontDomain;

    @Value("${aws.cloudfront.key.id}")
    private String keyPairId;

    @Value("${aws.cloudfront.private.key.path}")
    private String privateKeyPath;

    @Value("${aws.cloudfront.skin.domain:}")
    private String cloudFrontSkinDomain;

    @Value("${aws.cloudfront.skin.key.id:}")
    private String skinKeyPairId;

    @Value("${aws.cloudfront.skin.private.key.path:}")
    private String skinPrivateKeyPath;

    /**
     * CloudFront Signed URL 생성
     * @param objectPath CloudFront 기준 파일 경로 (예: /uploads/test.mp3)
     * @param expiresInSeconds 유효시간 (초 단위, 기본 10분)
     * @return Signed URL
     */
    public String createSignedUrl(String objectPath, int expiresInSeconds) {
        if (objectPath == null || objectPath.isEmpty()) {
            return "";
        }

        try {
            // 1. Private Key Path 생성
            Path keyPath = Paths.get(privateKeyPath);

            // 2. Resource URL 생성
            String resourceUrl = buildResourceUrl(cloudFrontDomain, objectPath);

            // 3. 만료 시간 계산
            Instant expirationDate = Instant.now().plus(expiresInSeconds, ChronoUnit.SECONDS);

            // 4. Signed URL 생성
            CloudFrontUtilities cloudFrontUtilities = CloudFrontUtilities.create();

            CannedSignerRequest signerRequest = CannedSignerRequest.builder()
                    .resourceUrl(resourceUrl)
                    .privateKey(keyPath)
                    .keyPairId(keyPairId)
                    .expirationDate(expirationDate)
                    .build();

            String signedUrl = cloudFrontUtilities.getSignedUrlWithCannedPolicy(signerRequest).url();

            log.debug("Created CloudFront signed URL for: {}", objectPath);
            return signedUrl;

        } catch (IOException e) {
            log.error("Failed to read private key from: {}", privateKeyPath, e);
            throw new RuntimeException("CloudFront signed URL creation failed", e);
        } catch (Exception e) {
            log.error("Failed to create CloudFront signed URL", e);
            throw new RuntimeException("CloudFront signed URL creation failed", e);
        }
    }

    /**
     * CloudFront Signed URL 생성 (기본 10분 유효)
     */
    public String createSignedUrl(String objectPath) {
        return createSignedUrl(objectPath, 600); // 10분
    }

    /**
     * Skin용 CloudFront Signed URL 생성
     * @param objectPath CloudFront 기준 파일 경로
     * @param expiresInSeconds 유효시간 (초 단위, 기본 3시간)
     * @return Signed URL
     */
    public String createSkinSignedUrl(String objectPath, int expiresInSeconds) {
        if (objectPath == null || objectPath.isEmpty()) {
            return "";
        }

        try {
            // 1. Private Key Path 생성
            Path keyPath = Paths.get(skinPrivateKeyPath);

            // 2. Resource URL 생성
            String resourceUrl = buildResourceUrl(cloudFrontSkinDomain, objectPath);

            // 3. 만료 시간 계산
            Instant expirationDate = Instant.now().plus(expiresInSeconds, ChronoUnit.SECONDS);

            // 4. Signed URL 생성
            CloudFrontUtilities cloudFrontUtilities = CloudFrontUtilities.create();

            CannedSignerRequest signerRequest = CannedSignerRequest.builder()
                    .resourceUrl(resourceUrl)
                    .privateKey(keyPath)
                    .keyPairId(skinKeyPairId)
                    .expirationDate(expirationDate)
                    .build();

            String signedUrl = cloudFrontUtilities.getSignedUrlWithCannedPolicy(signerRequest).url();

            log.debug("Created CloudFront Skin signed URL for: {}", objectPath);
            return signedUrl;

        } catch (IOException e) {
            log.error("Failed to read skin private key from: {}", skinPrivateKeyPath, e);
            throw new RuntimeException("CloudFront Skin signed URL creation failed", e);
        } catch (Exception e) {
            log.error("Failed to create CloudFront Skin signed URL", e);
            throw new RuntimeException("CloudFront Skin signed URL creation failed", e);
        }
    }

    /**
     * Skin용 CloudFront Signed URL 생성 (기본 3시간 유효)
     */
    public String createSkinSignedUrl(String objectPath) {
        return createSkinSignedUrl(objectPath, 10800); // 3시간
    }

    /**
     * Resource URL 생성 헬퍼
     */
    private String buildResourceUrl(String domain, String objectPath) {
        // objectPath가 '/'로 시작하지 않으면 추가
        String path = objectPath.startsWith("/") ? objectPath : "/" + objectPath;
        return "https://" + domain + path;
    }
}
