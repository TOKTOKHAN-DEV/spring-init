package com.spring.spring_init.common.apidocs

import com.spring.spring_init.common.base.BaseErrorCode
import com.spring.spring_init.common.dto.ErrorResponseDTO
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.examples.Example
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.responses.ApiResponses
import org.springframework.util.StringUtils
import org.springframework.web.method.HandlerMethod

object ApiExceptionExplainParser {

    @JvmStatic
    fun parse(
        operation: Operation,
        handlerMethod: HandlerMethod
    ) {
        val annotation = handlerMethod.getMethodAnnotation(ApiResponseExplanations::class.java)

        if (annotation != null) {
            generateExceptionResponseDocs(operation, annotation.errors)
        }
    }

    private fun generateExceptionResponseDocs(
        operation: Operation,
        exceptions: Array<ApiExceptionExplanation>
    ) {
        val responses = operation.responses

        val holders = exceptions
            .map { ExampleHolder.from(it) }
            .groupBy { it.httpStatus }

        addExamplesToResponses(responses, holders)
    }

    private data class ExampleHolder(
        val httpStatus: Int,
        val name: String,
        val mediaType: String,
        val description: String,
        val holder: Example
    ) {
        companion object {
            fun from(annotation: ApiExceptionExplanation): ExampleHolder {
                val errorCode = getErrorCode(annotation)

                return ExampleHolder(
                    httpStatus = errorCode.httpStatus.value(),
                    name = if (StringUtils.hasText(annotation.name)) annotation.name else errorCode.message,
                    mediaType = annotation.mediaType,
                    description = annotation.description,
                    holder = createExample(errorCode, annotation.summary, annotation.description)
                )
            }

            @Suppress("UNCHECKED_CAST")
            private fun getErrorCode(annotation: ApiExceptionExplanation): BaseErrorCode {
                val enumClass = annotation.value.java
                val enumConstants = enumClass.enumConstants
                return enumConstants.first { (it as Enum<*>).name == annotation.constant }
            }

            private fun createExample(
                errorCode: BaseErrorCode,
                summary: String,
                description: String
            ): Example {
                val response = ErrorResponseDTO(
                    errorCode.code,
                    errorCode.message
                )

                return Example().apply {
                    value = response
                    setSummary(summary)
                    setDescription(description)
                }
            }
        }
    }

    private fun addExamplesToResponses(
        responses: ApiResponses,
        holders: Map<Int, List<ExampleHolder>>
    ) {
        holders.forEach { (httpStatus, exampleHolders) ->
            val content = Content()
            val mediaType = MediaType()
            val response = io.swagger.v3.oas.models.responses.ApiResponse()

            exampleHolders.forEach { holder ->
                mediaType.addExamples(holder.name, holder.holder)
            }
            content.addMediaType("application/json", mediaType)
            response.content = content

            responses.addApiResponse(httpStatus.toString(), response)
        }
    }
}
