package org.example.messenger.service;

import org.example.messenger.domain.model.MediaFile;
import org.example.messenger.enumeration.MediaFileTypeEnum;
import org.springframework.web.multipart.MultipartFile;

public interface MediaService {

  MediaFile save(MultipartFile file, MediaFileTypeEnum fileType);

  MediaFile get(String id);

}
