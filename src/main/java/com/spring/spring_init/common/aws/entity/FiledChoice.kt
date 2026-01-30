package com.spring.spring_init.common.aws.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "파일 경로")
public enum FiledChoice {
	// TODO : 적절한 파일 경로 추가,
	USER("user", "유저 프로필 사진")
	
	;
	
	private final String value;
	private final String description;
	
	FiledChoice(String value, String description) {
		this.value = value;
		this.description = description;
	}
	
	public static FiledChoice of(String value) {
		for (FiledChoice filedChoice : FiledChoice.values()) {
			if (filedChoice.value.equals(value)) {
				return filedChoice;
			}
		}
		throw new IllegalArgumentException("Unknown FiledChoice value: " + value);
	}
}