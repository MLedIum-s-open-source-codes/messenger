package org.example.messenger.service;

import org.example.messenger.domain.response.FileUploadResponse;
import org.example.messenger.enumeration.MediaFileTypeEnum;
import org.springframework.web.multipart.MultipartFile;

public interface S3Service {

  FileUploadResponse uploadFile(MultipartFile file, MediaFileTypeEnum fileType);

}
