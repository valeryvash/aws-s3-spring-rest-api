package net.vash.awss3springrestapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Component
public class StorageConfig {

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;
    @Value("${cloud.aws.credentials.secret-key}")
    private String accessSecret;
    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.service.endpoint}")
    private String serviceEndpoint;

    @Bean
    public S3Client s3Client() {

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, accessSecret);

        return  S3Client
                .builder()
                .region(Region.of(region))
                .endpointOverride(URI.create(serviceEndpoint))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }
}
