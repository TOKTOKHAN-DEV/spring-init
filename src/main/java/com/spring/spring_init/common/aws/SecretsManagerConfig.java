package com.spring.spring_init.common.aws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;


@Configuration
public class SecretsManagerConfig {

    private final SecretsManagerClient secretsManagerClient;

    public SecretsManagerConfig(@Value("${aws.region.static}") String region) {
        this.secretsManagerClient = SecretsManagerClient.builder()
            .region(Region.of(region)) // 주입된 region 사용
            .build();
    }

    public String getSecret(String secretName) {
        GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse secretValueResponse =
            secretsManagerClient.getSecretValue(getSecretValueRequest);
        return secretValueResponse.secretString(); // JSON 형태로 반환
    }
}
