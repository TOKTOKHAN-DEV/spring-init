package com.spring.spring_init.common.aws.dto.response;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PresignedResponseDto {
	@Schema(
		description = "Presigned URL",
		example = "https://example-bucket.s3.amazonaws.com/presigned-url"
	)
	private String url;
	
	@Schema(
		description = "추가 필드 정보",
		example = "{ \"field1\": \"value1\", \"field2\": \"value2\" }"
	)
	private Map<String, String> fields;
	
	public PresignedResponseDto(String url) {
		this.url = url;
	}
}