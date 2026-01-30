package com.spring.spring_init.common.aws.controller

import com.spring.spring_init.common.aws.dto.request.PresignedRequestDto
import com.spring.spring_init.common.aws.dto.response.PresignedResponseDto
import com.spring.spring_init.common.aws.service.FileService
import com.spring.spring_init.common.dto.ResponseDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/v1/presigned_url/")
@RestController
class PresignedController(
    private val fileService: FileService
) : PresignedApi {

    @PostMapping
    override fun createPresignedUrl(
        @RequestBody requestDto: PresignedRequestDto
    ): ResponseEntity<ResponseDTO<PresignedResponseDto>> {
        return ResponseEntity.ok(
            ResponseDTO(
                statusCode = HttpStatus.OK.value(),
                message = "SUCCESS",
                data = fileService.createPresignedUrl(requestDto)
            )
        )
    }
}
