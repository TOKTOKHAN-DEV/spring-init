package com.spring.spring_init.common.aws.service;

import com.spring.spring_init.common.aws.dto.response.FileUploadResponseDto;
import com.spring.spring_init.common.aws.dto.response.PresignedUrlResponseDto;
import com.spring.spring_init.common.aws.exception.FileExceptionCode;
import com.spring.spring_init.common.exception.CommonException;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class AwsFileService {

    @Value("${aws.s3.bucket}")
    private String bucket;

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    // pre-signed URL 반환 (업로드)
    public PresignedUrlResponseDto getPreSignedUrlForUpload(String originFileName, String dirName) {
        String fileName = createFilePath(originFileName, dirName);
        Duration duration = Duration.ofMinutes(2);

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(duration)
            .putObjectRequest(
                req -> req.bucket(bucket).key(fileName)
            )
            .build();

        String url = s3Presigner.presignPutObject(presignRequest).url().toString();

        return new PresignedUrlResponseDto(url, Date.from(Instant.now().plus(duration)));
    }

    // pre-signed URL 반환 (다운로드)
    public PresignedUrlResponseDto getPreSignedUrlForDownload(String fileName) {
        Duration duration = Duration.ofMinutes(2);

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(duration)
            .getObjectRequest(
                req -> req.bucket(bucket).key(fileName)
            )
            .build();

        String url = s3Presigner.presignGetObject(presignRequest).url().toString();

        return new PresignedUrlResponseDto(url, Date.from(Instant.now().plus(duration)));
    }

    // 일반 파일 업로드
    public FileUploadResponseDto uploadFile(MultipartFile file, String dirName) {
        if (file.isEmpty()) {
            return null;
        }

        String fileName = createFilePath(file.getOriginalFilename(), dirName);

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .contentType(file.getContentType())
                .build();

            s3Client.putObject(
                request,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
        } catch (IOException e) {
            throw new CommonException(
                FileExceptionCode.FAIL_UPLOAD_FILE.getCode(),
                FileExceptionCode.FAIL_UPLOAD_FILE.getMessage()
            );
        }

        String fileUrl = String.format("https://%s.s3.amazonaws.com/%s", bucket, fileName);

        return new FileUploadResponseDto(fileName, fileUrl);
    }

    // 파일 다운로드
    public byte[] downloadFile(String fileName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(bucket)
            .key(fileName)
            .build();

        return s3Client.getObjectAsBytes(getObjectRequest).asByteArray();
    }

    // 파일 삭제
    public void deleteFile(String fileName) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
            .bucket(bucket)
            .key(fileName)
            .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    // ------------------ UTIL ------------------ //

    private String createFilePath(String fileName, String dirName) {
        String fileId = createFileId();
        return dirName + "/" + fileId + "_" + fileName;
    }

    private String createFileId() {
        return UUID.randomUUID().toString();
    }
}
