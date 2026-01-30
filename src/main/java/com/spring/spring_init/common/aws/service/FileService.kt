package com.spring.spring_init.common.aws.service

import com.spring.spring_init.common.aws.dto.request.PresignedRequestDto
import com.spring.spring_init.common.aws.dto.response.PresignedResponseDto
import com.spring.spring_init.common.aws.entity.FiledChoice
import com.spring.spring_init.common.aws.exception.FileExceptionCode
import com.spring.spring_init.common.exception.CommonException
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaTypeFactory
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.HeadObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.net.URL
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.time.Duration

@Service
class FileService(
    private val s3Client: S3Client,
    private val s3Presigner: S3Presigner
) {
    @Value("\${aws.s3.bucket}")
    private lateinit var bucketName: String

    fun createPresignedUrl(request: PresignedRequestDto): PresignedResponseDto {
        val uploadPath = getUploadPath(request.fieldChoices)
        val objectKey = getObjectKey(uploadPath, request.fileName)
        val contentType = getContentType(request.fileName)

        val presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(5))
            .putObjectRequest { req ->
                req
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType(contentType)
            }
            .build()

        val url = s3Presigner.presignPutObject(presignRequest).url().toString()
        return parsePresignedUrl(url, objectKey)
    }

    private fun getContentType(fileName: String): String {
        val mediaType = MediaTypeFactory.getMediaType(fileName)
        return mediaType.map { it.toString() }.orElse("application/octet-stream")
    }

    private fun getUploadPath(fieldChoice: FiledChoice): String {
        return "_media/" + fieldChoice.value
    }

    private fun getObjectKey(uploadPath: String, fileName: String): String {
        val objectKey = "$uploadPath/$fileName"

        if (objectExists(objectKey)) {
            val randomString = generateRandomString()
            val parts = fileName.split(".")
            val newFileName = "${parts[0]}_$randomString.${parts[1]}"
            return getObjectKey(uploadPath, newFileName)
        }

        return objectKey
    }

    private fun objectExists(objectKey: String): Boolean {
        return try {
            s3Client.headObject(
                HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build()
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun generateRandomString(): String {
        return RandomStringUtils.randomAlphanumeric(7)
    }

    private fun parsePresignedUrl(presignedUrl: String, objectKey: String): PresignedResponseDto {
        return try {
            val url = URL(presignedUrl)

            val fields = mutableMapOf<String, String>()
            val query = url.query
            if (query != null) {
                val pairs = query.split("&")
                for (pair in pairs) {
                    val idx = pair.indexOf("=")
                    val key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8)
                    val value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8)
                    fields[key] = value
                }
            }

            fields["key"] = objectKey

            PresignedResponseDto(presignedUrl, fields)

        } catch (e: Exception) {
            throw CommonException(
                FileExceptionCode.FAIL_UPLOAD_FILE.code,
                FileExceptionCode.FAIL_UPLOAD_FILE.message
            )
        }
    }
}
