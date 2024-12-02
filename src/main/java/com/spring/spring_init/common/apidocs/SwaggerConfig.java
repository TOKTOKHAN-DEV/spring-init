package com.spring.spring_init.common.apidocs;

import com.spring.spring_init.common.security.exception.AuthExceptionCode;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.Arrays;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {

        // OAuth2 설정 (프로젝트 로그인 사용)
        SecurityScheme oauthScheme = new SecurityScheme()
            .type(SecurityScheme.Type.OAUTH2)
            .flows(new OAuthFlows()
                .password(new OAuthFlow()
                    .tokenUrl("/v1/user/swagger-login")  //스웨거 로그인 api
                )
            );

        SecurityRequirement oauthRequirement = new SecurityRequirement().addList("System Login");

        /**
         * 프로젝트에 따라 별도 설정
         */
        Info info = new Info()
            .version("1.0.0")
            .title("Project Name")
            .description("""
                개발: [api.dev.my-domain.com](https://api.dev.my-domain.com)
                <br/>
                운영: [api.my-domain.com](https://api.my-domain.com)
                """); //도메인 명 바꾸기

        return new OpenAPI()
            .addServersItem(new Server().url("/"))
            .components(new Components()
                .addSecuritySchemes("System Login", oauthScheme)
            )
            .security(Arrays.asList(oauthRequirement))
            .info(info);
    }

    @Bean
    public OperationCustomizer customiseOperations() {
        return (operation, handlerMethod) -> {
            ApiResponses responses = operation.getResponses();

            // 401 응답 정의
            Schema<?> unauthorizedSchema = new Schema<>();
            unauthorizedSchema.setName("ErrorResponseDTO");
            unauthorizedSchema.setType("object");
            unauthorizedSchema.addProperties(
                "errorCode", new Schema<>()
                    .type("string")
                    .example(AuthExceptionCode.UNAUTHORIZED_ACCESS.getCode())
            );
            unauthorizedSchema.addProperties(
                "description",
                new Schema<>()
                    .type("string")
                    .example(AuthExceptionCode.UNAUTHORIZED_ACCESS.getMessage())
            );

            responses.addApiResponse("401", new ApiResponse()
                .description("Unauthorized")
                .content(new Content().addMediaType("application/json",
                    new MediaType().schema(unauthorizedSchema))
                )
            );

            Schema<?> accessDeniedSchema = new Schema<>();
            accessDeniedSchema.setName("ErrorResponseDTO");
            accessDeniedSchema.setType("object"); // 객체 타입 지정
            accessDeniedSchema.addProperties(
                "errorCode",
                new Schema<>()
                    .type("string")
                    .example(AuthExceptionCode.ACCESS_DENIED.getCode())
            );
            accessDeniedSchema.addProperties(
                "description",
                new Schema<>()
                    .type("string")
                    .example(AuthExceptionCode.ACCESS_DENIED.getMessage())
            );

            responses.addApiResponse("403", new ApiResponse()
                .description("Forbidden")
                .content(new Content().addMediaType("application/json",
                    new MediaType().schema(accessDeniedSchema))
                )
            );

            return operation;
        };
    }

    @Bean
    public OperationCustomizer customizer() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            ApiExceptionExplainParser.parse(operation, handlerMethod);
            return operation;
        };
    }
}
