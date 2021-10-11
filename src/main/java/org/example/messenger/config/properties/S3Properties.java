package org.example.messenger.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("s3.properties")
public class S3Properties {

  private String accessKey;
  private String secretKey;

  private String bucketName;
  private String region;

  private String publicUrl;

}
