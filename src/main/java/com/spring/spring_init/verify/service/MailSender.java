package com.spring.spring_init.verify.service;

import com.spring.spring_init.user.entity.User;
import com.spring.spring_init.verify.entity.EmailVerifyPurpose;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.Body;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.Message;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import software.amazon.awssdk.services.ses.model.SendEmailResponse;
import software.amazon.awssdk.services.ses.model.SesException;


@Slf4j
@Component
@RequiredArgsConstructor
public class MailSender {

    private final SesClient sesClient;
    private final TemplateEngine templateEngine;
    private final EmailTokenGenerator emailTokenGenerator;

    @Value("${common.domain}")
    private String domain;

    @Value("${common.site-name}")
    private String serviceName;

    public String sendEmail(
        final String to,
        final String code,
        final String token,
        final User user,
        final EmailVerifyPurpose purpose
    ) {
        String subject;
        String bodyText;
        String bodyHtml = null; // HTML body는 특정 목적일 때만 사용

        // 이메일 제목 및 본문 구성
        switch (purpose) {
            case EMAIL_VALIDATION:
                subject = serviceName + " 이메일 인증번호는 " + code + " 입니다.";
                bodyText = "이메일 인증을 위해 아래 인증번호 6자리를 입력해주세요.\n\n" + code;
                break;
            case RESET_PASSWORD:
                subject = serviceName + " 비밀번호 재설정 안내 메일입니다.";
                bodyText = "비밀번호 재설정 안내입니다. HTML 형식으로 제공됩니다.";
                bodyHtml = generatePasswordResetTemplate(
                    "http",
                    domain,
                    user,
                    token
                );
                break;
            default:
                throw new IllegalArgumentException("Unknown email purpose: " + purpose);
        }

        // SES 요청 객체 생성
        SendEmailRequest.Builder requestBuilder = SendEmailRequest.builder()
            .source("no-reply@pencily.net") // 보내는 이메일 주소
            .destination(Destination.builder()
                .toAddresses(to)
                .build())
            .message(Message.builder()
                .subject(Content.builder()
                    .data(subject)
                    .charset("UTF-8")
                    .build())
                .body(Body.builder()
                    .text(Content.builder()
                        .data(bodyText)
                        .charset("UTF-8")
                        .build())
                    .build()).build());

        // HTML 본문이 있으면 설정
        if (bodyHtml != null) {
            requestBuilder.message(Message.builder()
                .subject(Content.builder()
                    .data(subject)
                    .charset("UTF-8")
                    .build())
                .body(Body.builder()
                    .html(Content.builder()
                        .data(bodyHtml)
                        .charset("UTF-8")
                        .build())
                    .text(Content.builder()
                        .data(bodyText)
                        .charset("UTF-8")
                        .build())
                    .build()).build());
        }

        // 이메일 발송
        try {
            SendEmailRequest sendEmailRequest = requestBuilder.build();
            SendEmailResponse response = sesClient.sendEmail(sendEmailRequest);
            log.info("Email sent successfully: {}", response.messageId());
            return response.messageId();
        } catch (SesException e) {
            log.error("Failed to send email: {}", e.getMessage());
            throw new RuntimeException("Email sending failed", e);
        }
    }

    //비밀번호 재설정 메일 템플릿 생성(thymeleaf 사용)
    private String generatePasswordResetTemplate(
        final String protocol,
        final String domain,
        final User user,
        final String token
    ) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
            "yyyy년 MM월 dd일 a hh:mm",
            Locale.KOREAN
        );
        String expireTime = now.format(formatter);

        Context context = new Context();
        context.setVariable("protocol", protocol);
        context.setVariable("domain", domain);
        context.setVariable("penName", user.getPenName());
        context.setVariable("uid", emailTokenGenerator.generateUidByUserId(user.getUserId()));
        context.setVariable("token", token);
        context.setVariable("expireTime", expireTime);

        return templateEngine.process("password_reset_email", context);
    }
}
