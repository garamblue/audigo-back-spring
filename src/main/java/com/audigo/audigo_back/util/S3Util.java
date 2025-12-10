package com.audigo.audigo_back.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * AWS S3 파일 업로드/삭제 유틸리티
 */
@Component
@Slf4j
public class S3Util {

    @Value("${aws.s3.region:ap-northeast-2}")
    private String awsRegion;

    @Value("${aws.s3.access.key.id}")
    private String awsAccessKeyId;

    @Value("${aws.s3.secret.access.key}")
    private String awsSecretAccessKey;

    @Value("${aws.s3.bucket}")
    private String s3Bucket;

    @Value("${aws.s3.skin.bucket:}")
    private String s3SkinBucket;

    private S3Client s3Client;

    @PostConstruct
    public void init() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                awsAccessKeyId,
                awsSecretAccessKey
        );

        this.s3Client = S3Client.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    /**
     * S3 URL에서 키(파일 경로) 추출
     */
    public String extractS3Key(String url) {
        try {
            URL urlObj = new URL(url);
            String prefix = s3Bucket + ".s3." + awsRegion + ".amazonaws.com/";
            String href = urlObj.toString();
            int idx = href.indexOf(prefix);

            if (idx != -1) {
                return href.substring(idx + prefix.length());
            }

            // pathname에서 추출 (leading slash 제거)
            String path = urlObj.getPath();
            return path.startsWith("/") ? path.substring(1) : path;

        } catch (Exception e) {
            log.warn("Failed to parse S3 URL: {}, returning as-is", url);
            return url;
        }
    }

    /**
     * MultipartFile을 S3에 업로드
     * @param file MultipartFile
     * @param folder S3 폴더 경로
     * @return S3 파일 URL
     */
    public String uploadToS3(MultipartFile file, String folder) {
        try {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                throw new IllegalArgumentException("Original filename is null");
            }

            // 파일 확장자 추출
            int lastDotIndex = originalFilename.lastIndexOf('.');
            String ext = lastDotIndex > 0 ? originalFilename.substring(lastDotIndex) : "";
            String basename = lastDotIndex > 0 ? originalFilename.substring(0, lastDotIndex) : originalFilename;

            // 파일명 생성 (타임스탬프 추가)
            String fileName = String.format("%s/%s-%d%s", folder, basename, System.currentTimeMillis(), ext);

            // S3에 업로드
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(s3Bucket)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            log.info("Uploaded to S3: {}", fileName);

            // 파일 URL 생성
            return String.format("https://%s.s3.%s.amazonaws.com/%s", s3Bucket, awsRegion, fileName);

        } catch (IOException e) {
            log.error("S3 upload error", e);
            throw new RuntimeException("S3 upload failed", e);
        }
    }

    /**
     * 로컬 파일을 S3에 업로드 (Skin용)
     * @param filePath 로컬 파일 전체 경로
     * @param folder S3 폴더 경로
     * @return S3 파일 URL
     */
    public String uploadToSkinS3(String filePath, String folder) {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                throw new IllegalArgumentException("File not found: " + filePath);
            }

            String filename = path.getFileName().toString();
            int lastDotIndex = filename.lastIndexOf('.');
            String ext = lastDotIndex > 0 ? filename.substring(lastDotIndex) : "";
            String basename = lastDotIndex > 0 ? filename.substring(0, lastDotIndex) : filename;

            // 파일명 생성 (타임스탬프 없이)
            String s3FileName = String.format("%s/%s%s", folder, basename, ext);

            // MIME 타입 추론
            String mimeType = getMimeType(ext);

            // S3에 업로드
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(s3SkinBucket)
                    .key(s3FileName)
                    .contentType(mimeType)
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromFile(path));

            log.info("Uploaded to Skin S3: {}", s3FileName);

            // 파일 URL 생성
            return String.format("https://%s.s3.%s.amazonaws.com/%s", s3SkinBucket, awsRegion, s3FileName);

        } catch (Exception e) {
            log.error("S3 Skin upload error for: {}", filePath, e);
            throw new RuntimeException("S3 Skin upload failed", e);
        }
    }

    /**
     * S3에서 파일 삭제
     * @param keyUrl S3 URL 또는 키
     * @return 삭제 성공 여부
     */
    public boolean deleteFromS3(String keyUrl) {
        try {
            String key = extractS3Key(keyUrl);

            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(s3Bucket)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteRequest);

            log.info("Deleted from S3: {}", key);
            return true;

        } catch (Exception e) {
            log.error("S3 delete error", e);
            throw new RuntimeException("S3 delete failed", e);
        }
    }

    /**
     * 파일 확장자로 MIME 타입 추론
     */
    private String getMimeType(String ext) {
        String lowerExt = ext.toLowerCase();
        switch (lowerExt) {
            case ".jpg":
            case ".jpeg":
                return "image/jpeg";
            case ".png":
                return "image/png";
            case ".gif":
                return "image/gif";
            case ".webp":
                return "image/webp";
            case ".pdf":
                return "application/pdf";
            case ".mp3":
                return "audio/mpeg";
            case ".mp4":
                return "video/mp4";
            case ".json":
                return "application/json";
            default:
                return "application/octet-stream";
        }
    }
}
