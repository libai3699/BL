package com.common.core.mail;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import com.common.core.nacos.BNacosParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.regions.Region;

import javax.annotation.Resource;


/**
 * @author xx
 * @date 2024/2/27 14:55
 */
@Component
@Slf4j
@Service
public class EmailSender {
    private static final Region REGION = Region.US_EAST_1; // e.g., "us-west-2"
    private String  FROM_ADDRESS = "egame@egametest.awsapps.com";
    @Resource
    private BNacosParam bNacosParam;



}
