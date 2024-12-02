package com.spring.spring_init.common.aws.controller;

import com.spring.spring_init.common.aws.dto.response.FileUploadResponseDto;
import com.spring.spring_init.common.aws.dto.response.PresignedUrlResponseDto;
import com.spring.spring_init.common.aws.service.AwsFileService;
import com.spring.spring_init.common.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/test/s3")
@RequiredArgsConstructor
public class S3TestController implements S3TestApi {

    private final AwsFileService awsFileService;

    // pre-signed URL 반환 (업로드)
    @GetMapping("/presigned-url/upload")
    public ResponseEntity<ResponseDTO<PresignedUrlResponseDto>> getPresignedUrlForUpload(
        @RequestParam("originFileName") String originFileName,
        @RequestParam("dirName") String dirName
    ) {
        return ResponseEntity.ok(
            ResponseDTO.<PresignedUrlResponseDto>builder()
                .statusCode(200)
                .message("SUCCESS")
                .data(awsFileService.getPreSignedUrlForUpload(originFileName, dirName))
                .build()
        );
    }

    // pre-signed URL 반환 (다운로드)
    @GetMapping("/presigned-url/download")
    public ResponseEntity<ResponseDTO<PresignedUrlResponseDto>> getPresignedUrlForDownload(
        @RequestParam("fileName") String fileName
    ) {
        return ResponseEntity.ok(
            ResponseDTO.<PresignedUrlResponseDto>builder()
                .statusCode(200)
                .message("SUCCESS")
                .data(awsFileService.getPreSignedUrlForDownload(fileName))
                .build()
        );
    }

    // 파일 업로드
    @PostMapping("/upload")
    public ResponseEntity<ResponseDTO<FileUploadResponseDto>> uploadFile(
        @RequestParam("file") MultipartFile file,
        @RequestParam("dirName") String dirName
    ) {
        return ResponseEntity.ok(
            ResponseDTO.<FileUploadResponseDto>builder()
                .statusCode(200)
                .message("SUCCESS")
                .data(awsFileService.uploadFile(file, dirName))
                .build()
        );
    }

    // 파일 다운로드
    @GetMapping("/download")
    public ResponseEntity<ResponseDTO<byte[]>> downloadFile(
        @RequestParam("fileName") String fileName
    ) {
        return ResponseEntity.ok(
            ResponseDTO.<byte[]>builder()
                .statusCode(200)
                .message("SUCCESS")
                .data(awsFileService.downloadFile(fileName))
                .build()
        );
    }

    // 파일 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDTO<Void>> deleteFile(
        @RequestParam("fileName") String fileName
    ) {
        awsFileService.deleteFile(fileName);
        return ResponseEntity.ok(
            ResponseDTO.<Void>builder()
                .statusCode(200)
                .message("SUCCESS")
                .build()
        );
    }

}
