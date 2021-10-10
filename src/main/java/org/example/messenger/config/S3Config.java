package org.example.messenger.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.RequiredArgsConstructor;
import org.example.messenger.config.properties.S3Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class S3Config {

  private final S3Properties s3Properties;

  @Bean
  public AmazonS3 s3Client() {
    BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(s3Properties.getAccessKey(), s3Properties.getSecretKey());
    return AmazonS3ClientBuilder.standard()
        .withRegion(s3Properties.getRegion())
        .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
        .build();
  }

}
