package com.spring.spring_init.common.aws.dto.request;

import com.spring.spring_init.common.aws.entity.FileType;
import com.spring.spring_init.common.aws.entity.FiledChoice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PresignedRequestDto {
	@Schema(
		description = "파일명",
		requiredMode = Schema.RequiredMode.REQUIRED,
		example = "profile.jpg",
		minLength = 1
	)
	private String fileName;
	
	@Schema(
		description = "파일 타입",
		requiredMode = Schema.RequiredMode.REQUIRED,
		example = "IMAGE"
	)
	private FileType fileType;
	
	@Schema(
		description = "파일 경로",
		requiredMode = Schema.RequiredMode.REQUIRED,
		example = "USER_THUMBNAIL"
	)
	private FiledChoice fieldChoices;
}