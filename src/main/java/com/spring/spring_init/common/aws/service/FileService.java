package com.spring.spring_init.common.aws.service;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Service;

import com.spring.spring_init.common.aws.dto.request.PresignedRequestDto;
import com.spring.spring_init.common.aws.dto.response.PresignedResponseDto;
import com.spring.spring_init.common.aws.entity.FiledChoice;
import com.spring.spring_init.common.aws.exception.FileExceptionCode;
import com.spring.spring_init.common.exception.CommonException;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class FileService {
    @Value("${aws.s3.bucket}")
    private String bucketName;

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    
    public PresignedResponseDto createPresignedUrl(PresignedRequestDto request) {
        String uploadPath = getUploadPath(request.getFieldChoices());
        String objectKey = getObjectKey(uploadPath, request.getFileName());
        String contentType = getContentType(request.getFileName());
        
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(5))
            .putObjectRequest(
                req -> req
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType(contentType)
            )
            .build();
        
        String url = s3Presigner.presignPutObject(presignRequest).url().toString();
        return parsePresignedUrl(url, objectKey);
    }
    
    private String getContentType(String fileName) {
        Optional<MediaType> mediaType = MediaTypeFactory.getMediaType(fileName);
        return mediaType.map(MediaType::toString).orElse("application/octet-stream");
    }
    
    private String getUploadPath(FiledChoice fieldChoice) {
        return "_media/" + fieldChoice.getValue();
    }
    
    private String getObjectKey(String uploadPath, String fileName) {
        String objectKey = uploadPath + "/" + fileName;
        
        if (objectExists(objectKey)) {
            String randomString = generateRandomString();
            String[] parts = fileName.split("\\.");
            String newFileName = parts[0] + "_" + randomString + "." + parts[1];
            return getObjectKey(uploadPath, newFileName);
        }
        
        return objectKey;
    }
    
    private boolean objectExists(String objectKey) {
        try {
            s3Client.headObject(HeadObjectRequest.builder().bucket(bucketName).key(objectKey).build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private String generateRandomString() {
        return RandomStringUtils.randomAlphanumeric(7);
    }
    
    private PresignedResponseDto parsePresignedUrl(String presignedUrl, String objectKey) {
        try {
            URL url = new URL(presignedUrl);
            
            Map<String, String> fields = new HashMap<>();
            String query = url.getQuery();
            if (query != null) {
                String[] pairs = query.split("&");
                for (String pair : pairs) {
                    int idx = pair.indexOf("=");
                    String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                    String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
                    fields.put(key, value);
                }
            }
            
            fields.put("key", objectKey);
            
            return new PresignedResponseDto(presignedUrl, fields);
            
        } catch (Exception e) {
            throw new CommonException(
                FileExceptionCode.FAIL_UPLOAD_FILE.getCode(),
                FileExceptionCode.FAIL_UPLOAD_FILE.getMessage()
            );
        }
    }
}