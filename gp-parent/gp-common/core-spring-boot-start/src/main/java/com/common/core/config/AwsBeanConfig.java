package com.common.core.config;

import com.common.core.prop.AmazonProp;
import com.common.core.util.AmazonService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * @author axing
 */
@Configuration
@EnableConfigurationProperties(AmazonProp.class)
public class AwsBeanConfig {

    @Bean
    public AmazonService amazonService(AmazonProp amazonProp) {
        AmazonService amazonService = new AmazonService();
        AwsCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(AwsBasicCredentials.create(amazonProp.getAccessKeyId(), amazonProp.getAccessKeySecret()));
        amazonService.setS3client(S3Client.builder()
                .credentialsProvider(credentialsProvider)
                .region(Region.AP_SOUTHEAST_1)
                .build());
        amazonService.setAmazonProp(amazonProp);
        return amazonService;
    }




}
