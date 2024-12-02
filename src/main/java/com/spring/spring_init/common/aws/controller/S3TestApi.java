package com.spring.spring_init.common.aws.controller;

import com.spring.spring_init.common.apidocs.ApiExceptionExplanation;
import com.spring.spring_init.common.apidocs.ApiResponseExplanations;
import com.spring.spring_init.common.aws.dto.response.FileUploadResponseDto;
import com.spring.spring_init.common.aws.dto.response.PresignedUrlResponseDto;
import com.spring.spring_init.common.aws.exception.FileExceptionCode;
import com.spring.spring_init.common.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jdk.jfr.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "[S3 Test]")
@Description("S3 작동 테스트 API")
public interface S3TestApi {

    @Operation(summary = "Presigned URL 반환 (업로드)")
    ResponseEntity<ResponseDTO<PresignedUrlResponseDto>> getPresignedUrlForUpload(
        @RequestParam("originFileName") String originFileName,
        @RequestParam("dirName") String dirName
    );

    @Operation(summary = "Presigned URL 반환 (다운로드)")
    ResponseEntity<ResponseDTO<PresignedUrlResponseDto>> getPresignedUrlForDownload(
        @RequestParam("fileName") String fileName
    );

    @Operation(summary = "파일 업로드")
    @ApiResponseExplanations(
        errors = {
            @ApiExceptionExplanation(
                name = "파일 업로드 실패",
                value = FileExceptionCode.class,
                constant = "FAIL_UPLOAD_FILE"
            )
        }
    )
    ResponseEntity<ResponseDTO<FileUploadResponseDto>> uploadFile(
        @RequestParam("file") MultipartFile file,
        @RequestParam("dirName") String dirName
    );

    @Operation(summary = "파일 다운로드")
    ResponseEntity<ResponseDTO<byte[]>> downloadFile(
        @RequestParam("fileName") String fileName
    );

    @Operation(summary = "파일 삭제")
    ResponseEntity<ResponseDTO<Void>> deleteFile(
        @RequestParam("fileName") String fileName
    );
}
