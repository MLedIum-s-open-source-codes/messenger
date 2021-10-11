package org.example.messenger.service.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectTagging;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.example.messenger.config.properties.S3Properties;
import org.example.messenger.domain.response.FileUploadResponse;
import org.example.messenger.enumeration.ErrorTypeEnum;
import org.example.messenger.enumeration.MediaFileTypeEnum;
import org.example.messenger.exception.CustomException;
import org.example.messenger.service.S3Service;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

  private final AmazonS3 amazonS3;
  private final S3Properties s3Properties;

  private static final Tag PUBLIC_TAG = new Tag("public", "true");
  private static final Tag DELETED_TAG = new Tag("deleted", "false");
  private static final List<Tag> NEW_FILE_TAGS = List.of(PUBLIC_TAG, DELETED_TAG);

  @Override
  public FileUploadResponse uploadFile(MultipartFile multipartFile, MediaFileTypeEnum fileType) {

    File file = convertMultiPartFileToFile(multipartFile);
    String fileName = getUniqueFileName(multipartFile, fileType);
    String folderName = getFolderName(fileType);

    PutObjectRequest putObjectRequest = new PutObjectRequest(s3Properties.getBucketName() + "/" + folderName, fileName, file)
        .withTagging(new ObjectTagging(NEW_FILE_TAGS));

    try {
      log.info("Uploading file: " + fileName);
      amazonS3.putObject(putObjectRequest);
      log.info("File upload is completed: " + fileName);

      file.delete();

      return FileUploadResponse.builder()
          .fileNameInStorage(fileName)
          .url(s3Properties.getPublicUrl() + folderName + "/" + fileName)
          .build();

    } catch (AmazonServiceException ex) {
      log.info("File upload is failed: " + fileName);
      log.error("Error: {} while uploading file.", ex.getMessage());
      throw new CustomException(ErrorTypeEnum.SAVE_TO_STORAGE, "File upload is failed.");
    }
  }

  private File convertMultiPartFileToFile(MultipartFile multipartFile) {
    File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));

    try (FileOutputStream outputStream = new FileOutputStream(file)) {
      outputStream.write(multipartFile.getBytes());
    } catch (IOException ex) {
      throw new CustomException(ErrorTypeEnum.INTERNAL_SERVER, "File converting error");
    }

    return file;
  }

  private String getUniqueFileName(MultipartFile multipartFile, MediaFileTypeEnum fileType) {
    String fileFormat;
    switch (fileType) {
      case IMAGE -> {
        if (multipartFile.getContentType().endsWith("gif")) {
          fileFormat = "gif";
        } else {
          fileFormat = "jpg";
        }
      }
      case VIDEO -> fileFormat = "mp4";
      case AUDIO -> fileFormat = "mp3";
      default -> fileFormat = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
    }

    return UUID.randomUUID().toString().replaceAll("-", "") + "." + fileFormat;
  }

  private String getFolderName(MediaFileTypeEnum fileType) {
    switch (fileType) {
      case IMAGE -> {
        return "imgs";
      }
      case VIDEO -> {
        return "videos";
      }
      case AUDIO -> {
        return "audios";
      }
      default -> {
        return "documents";
      }
    }
  }

}
