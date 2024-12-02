package com.spring.spring_init.verify.controller;

import com.spring.spring_init.common.dto.ResponseDTO;
import com.spring.spring_init.common.security.user.UserDetailsImpl;
import com.spring.spring_init.verify.dto.request.VerifyPasswordRequestDto;
import com.spring.spring_init.verify.service.VerifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/verifier")
public class VerifyController implements VerifyApi {

    private final VerifyService verifyService;

    @PostMapping("/password")
    public ResponseEntity<ResponseDTO<Void>> verifyPassword(
        @Validated @RequestBody VerifyPasswordRequestDto request,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        verifyService.verifyPassword(request, userDetails);

        return ResponseEntity.ok(
            ResponseDTO.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("SUCCESS")
                .build()
        );
    }
}
