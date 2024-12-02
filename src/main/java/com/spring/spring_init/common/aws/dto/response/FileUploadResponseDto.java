package com.spring.spring_init.common.aws.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponseDto {

    private String fileName;
    private String fileUrl;
}
