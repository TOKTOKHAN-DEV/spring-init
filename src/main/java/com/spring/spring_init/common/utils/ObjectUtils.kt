package com.spring.spring_init.common.utils

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.function.Function

@Component
class ObjectUtils {

    companion object {
        @JvmStatic
        private var S3_BUCKET_FULL_PATH: String = ""

        /**
         * Null-safe 반환
         * - 주로 ResponseDTO 객체에서 사용되는 메서드로, 전달 받은 값을 그대로 반환합니다.
         * @param value : 전달 받은 value
         * @return 전달 받은 value를 그대로 반환
         * @param <T> : 반환 타입
         */
        @JvmStatic
        fun <T> safe(value: T): T = value

        /**
         * Null-safe default value
         * @param value : 전달 받은 value
         * @param defaultValue : value가 null일 때 반환할 기본값
         * @return 전달 받은 value가 null이면 defaultValue를 반환하고, 그렇지 않으면 value를 반환
         * @param <T> : 반환 타입
         */
        @JvmStatic
        fun <T> defaultIfNull(value: T?, defaultValue: T): T = value ?: defaultValue

        /**
         * Null-safe mapping
         * @param source : 전달 받은 source 객체
         * @param mapper : source 객체를 변환하는 함수
         * @return 전달 받은 source가 null이 아니면 mapper를 적용한 결과를 반환하고, 그렇지 않으면 null을 반환
         * @param <T> : source 객체의 타입
         * @param <R> : 변환된 결과의 타입
         */
        @JvmStatic
        fun <T, R> safeGet(source: T?, mapper: Function<T, R>): R? {
            return source?.let { mapper.apply(it) }
        }

        /**
         * Image URL을 빈 문자열로 변환
         * @param imageUrl : 이미지 URL
         * @return 이미지 URL이 null이거나 빈 문자열이면 null을 반환하고, 그렇지 않으면 S3_BUCKET_FULL_PATH와 결합한 URL을 반환
         */
        @JvmStatic
        fun buildImageUrlOrNull(imageUrl: String?): String? {
            return if (imageUrl.isNullOrBlank()) {
                null
            } else {
                "$S3_BUCKET_FULL_PATH/$imageUrl"
            }
        }
    }

    @Value("\${aws.s3.bucket-full-path}")
    fun setS3BucketFullPath(value: String) {
        S3_BUCKET_FULL_PATH = value
    }
}
