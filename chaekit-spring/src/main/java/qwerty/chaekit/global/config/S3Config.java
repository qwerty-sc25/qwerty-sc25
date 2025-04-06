package qwerty.chaekit.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import qwerty.chaekit.global.properties.AwsProperties;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {
    private final String accessKeyId;
    private final String secretAccessKey;
    private final String region;

    public S3Config(AwsProperties awsProperties) {
        this.accessKeyId = awsProperties.accessKeyId();
        this.secretAccessKey = awsProperties.secretAccessKey();
        this.region = awsProperties.s3Region();
    }

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        AwsCredentialsProvider provider = StaticCredentialsProvider.create(credentials);

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(provider)
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        AwsCredentialsProvider provider = StaticCredentialsProvider.create(credentials);

        return S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(provider)
                .build();
    }
}