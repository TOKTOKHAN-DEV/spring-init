package com.spring.spring_init.common.aws.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.spring_init.common.aws.dto.request.PresignedRequestDto;
import com.spring.spring_init.common.aws.dto.response.PresignedResponseDto;
import com.spring.spring_init.common.aws.service.FileService;
import com.spring.spring_init.common.dto.ResponseDTO;

import lombok.RequiredArgsConstructor;

@RequestMapping("/v1/presigned_url/")

@RestController
@RequiredArgsConstructor
public class PresignedController implements PresignedApi {
	private final FileService fileService;
	
	@PostMapping
	public ResponseEntity<ResponseDTO<PresignedResponseDto>> createPresignedUrl(
		@RequestBody PresignedRequestDto requestDto
	) {
		return ResponseEntity.ok(
			ResponseDTO.<PresignedResponseDto>builder()
				.statusCode(HttpStatus.OK.value())
				.message("SUCCESS")
				.data(fileService.createPresignedUrl(requestDto))
				.build()
		);
	}
}