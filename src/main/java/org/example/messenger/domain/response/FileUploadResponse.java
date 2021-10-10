package org.example.messenger.domain.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileUploadResponse {

  private final String url;
  private final String fileNameInStorage;

}
