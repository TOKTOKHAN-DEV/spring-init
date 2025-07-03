package com.spring.spring_init.common.aws.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import com.spring.spring_init.common.aws.dto.request.PresignedRequestDto;
import com.spring.spring_init.common.aws.dto.response.PresignedResponseDto;
import com.spring.spring_init.common.dto.ResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "[Presigned API]")
public interface PresignedApi {
	@Operation(summary = "미리 서명된 URL 발급",
		description = """
			## Presigned URL 발급 API
			1. 해당 API 호출 시, 미리 서명된 URL을 발급해 반환
				- method: POST
				- url: /v1/presigned_url/
				- body: PresignedRequestDto (파일명, 파일 타입, 파일 경로 정보 포함)
				- 반환값: PresignedResponseDto (발급된 URL 및 필드 정보 포함)
			
			2. 반환된 URL로 파일 업로드
				- method: PUT
				- url: [발급된 URL]
				- body: 업로드할 파일 (binary / file 형식으로 전송)
			
			### 주의사항
			⚠️ Spring 서버의 Presigned URL API는 기존 Django 서버와의 동작 방식에 차이가 있으므로, <b>프론트엔드 코드에 수정이 필요합니다!</b> ⚠️
				- Spring 서버 (S3 Presigned URL 사용):
				- PUT 메서드 사용
				- Content-Type을 파일 MIME 타입으로 설정
				- 본문에 바이너리 데이터 직접 전송
				- multipart/form-data 사용 ❌
			"""
	)
	ResponseEntity<ResponseDTO<PresignedResponseDto>> createPresignedUrl(
		@RequestBody PresignedRequestDto presignedRequestDto
	);
}