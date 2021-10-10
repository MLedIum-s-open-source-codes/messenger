package org.example.messenger.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.example.messenger.domain.model.MediaFile;
import org.example.messenger.domain.response.FileUploadResponse;
import org.example.messenger.enumeration.ErrorTypeEnum;
import org.example.messenger.enumeration.MediaFileTypeEnum;
import org.example.messenger.exception.CustomException;
import org.example.messenger.repository.MediaRepository;
import org.example.messenger.service.MediaService;
import org.example.messenger.service.S3Service;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

  private final MediaRepository mediaRepository;
  private final S3Service fileStorageService;

  @Override
  public MediaFile save(MultipartFile file, MediaFileTypeEnum fileType) {

    if (fileType == null)
        fileType = MediaFileTypeEnum.ofContentType(file.getContentType());

    FileUploadResponse fileUploadResponse = fileStorageService.uploadFile(file, fileType);

    MediaFile mediaFile = MediaFile.builder()
        .title(file.getOriginalFilename())
        .url(fileUploadResponse.getUrl())
        .fileNameInStorage(fileUploadResponse.getFileNameInStorage())
        .type(fileType)
        .build();

    return mediaRepository.save(mediaFile);
  }

  @Override
  public MediaFile get(String id) {
    Optional<MediaFile> optional = mediaRepository.findById(id);
    if (optional.isEmpty())
        throw new CustomException(ErrorTypeEnum.NOT_FOUND, "File was not found");

    return optional.get();
  }

}
