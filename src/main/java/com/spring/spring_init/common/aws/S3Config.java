package com.spring.spring_init.common.aws;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {

	@Value("${aws.credentials.access-key}")
	private String accessKey;

	@Value("${aws.credentials.secret-key}")
	private String secretKey;

	@Value("${aws.region.static}")
	private String region;

	private AwsBasicCredentials awsBasicCredentials;

	@PostConstruct
	private void getAwsBasicCredentials() {
		this.awsBasicCredentials = AwsBasicCredentials.create(accessKey, secretKey);
	}

	@Bean
	public S3Client s3Client() {
		return S3Client.builder()
			.region(Region.of(region))
			.credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
			// 만약 실행중인 환경에서 AWS의 자격 증명을 가져오려면 위 코드 대신 아래 아래 코드를 사용하면 된다.
			// .credentialsProvider(DefaultCredentialsProvider.create())
			.build();
	}

	@Bean
	public S3Presigner s3Presigner() {
		return S3Presigner.builder()
			.region(Region.of(region))
			.credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
			.build();
	}
}
