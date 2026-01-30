package com.spring.spring_init.verify.service

import com.spring.spring_init.user.entity.User
import com.spring.spring_init.verify.entity.EmailVerifyPurpose
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import software.amazon.awssdk.services.ses.SesClient
import software.amazon.awssdk.services.ses.model.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class MailSender(
    private val sesClient: SesClient,
    private val templateEngine: TemplateEngine,
    private val emailTokenGenerator: EmailTokenGenerator
) {
    companion object {
        private val log = LoggerFactory.getLogger(MailSender::class.java)
    }

    @Value("\${common.domain}")
    private lateinit var domain: String

    @Value("\${common.site-name}")
    private lateinit var serviceName: String

    fun sendEmail(
        to: String,
        code: String,
        token: String,
        user: User?,
        purpose: EmailVerifyPurpose
    ): String {
        val (subject, bodyText, bodyHtml) = when (purpose) {
            EmailVerifyPurpose.EMAIL_VALIDATION -> {
                Triple(
                    "$serviceName 이메일 인증번호는 $code 입니다.",
                    "이메일 인증을 위해 아래 인증번호 6자리를 입력해주세요.\n\n$code",
                    null
                )
            }
            EmailVerifyPurpose.RESET_PASSWORD -> {
                Triple(
                    "$serviceName 비밀번호 재설정 안내 메일입니다.",
                    "비밀번호 재설정 안내입니다. HTML 형식으로 제공됩니다.",
                    generatePasswordResetTemplate("http", domain, user, token)
                )
            }
        }

        // SES 요청 객체 생성
        var requestBuilder = SendEmailRequest.builder()
            .source("no-reply@pencily.net") // 보내는 이메일 주소
            .destination(
                Destination.builder()
                    .toAddresses(to)
                    .build()
            )
            .message(
                Message.builder()
                    .subject(
                        Content.builder()
                            .data(subject)
                            .charset("UTF-8")
                            .build()
                    )
                    .body(
                        Body.builder()
                            .text(
                                Content.builder()
                                    .data(bodyText)
                                    .charset("UTF-8")
                                    .build()
                            )
                            .build()
                    ).build()
            )

        // HTML 본문이 있으면 설정
        if (bodyHtml != null) {
            requestBuilder = requestBuilder.message(
                Message.builder()
                    .subject(
                        Content.builder()
                            .data(subject)
                            .charset("UTF-8")
                            .build()
                    )
                    .body(
                        Body.builder()
                            .html(
                                Content.builder()
                                    .data(bodyHtml)
                                    .charset("UTF-8")
                                    .build()
                            )
                            .text(
                                Content.builder()
                                    .data(bodyText)
                                    .charset("UTF-8")
                                    .build()
                            )
                            .build()
                    ).build()
            )
        }

        // 이메일 발송
        return try {
            val sendEmailRequest = requestBuilder.build()
            val response = sesClient.sendEmail(sendEmailRequest)
            log.info("Email sent successfully: {}", response.messageId())
            response.messageId()
        } catch (e: SesException) {
            log.error("Failed to send email: {}", e.message)
            throw RuntimeException("Email sending failed", e)
        }
    }

    //비밀번호 재설정 메일 템플릿 생성(thymeleaf 사용)
    private fun generatePasswordResetTemplate(
        protocol: String,
        domain: String,
        user: User?,
        token: String
    ): String {
        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern(
            "yyyy년 MM월 dd일 a hh:mm",
            Locale.KOREAN
        )
        val expireTime = now.format(formatter)

        val context = Context()
        context.setVariable("protocol", protocol)
        context.setVariable("domain", domain)
        context.setVariable("uid", emailTokenGenerator.generateUidByUserId(user!!.userId!!))
        context.setVariable("token", token)
        context.setVariable("expireTime", expireTime)

        return templateEngine.process("password_reset_email", context)
    }
}
