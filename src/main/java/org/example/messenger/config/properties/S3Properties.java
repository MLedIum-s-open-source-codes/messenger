package org.example.messenger.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("s3.properties")
public class S3Properties {

  private final String accessKey;
  private final String secretKey;

  private final String bucketName;
  private final String region;

  private final String publicUrl;

}
