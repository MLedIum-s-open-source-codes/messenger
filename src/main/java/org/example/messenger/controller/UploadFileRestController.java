package org.example.messenger.controller;

import lombok.RequiredArgsConstructor;
import org.example.messenger.annotation.UserId;
import org.example.messenger.domain.dto.MediaFileDto;
import org.example.messenger.enumeration.MediaFileTypeEnum;
import org.example.messenger.service.MediaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Secured({"ROLE_USER"})
@RequestMapping("/files")
@RequiredArgsConstructor
public class UploadFileRestController {

  private final MediaService mediaService;

  @PostMapping
  public ResponseEntity<MediaFileDto> upload(
      @RequestBody MultipartFile file,
      @RequestParam(defaultValue = "false", required = false) Boolean isDocument,
      @UserId String userId
  ) {
    MediaFileTypeEnum fileTypeEnum = isDocument ? MediaFileTypeEnum.DOCUMENT : null;

    return ResponseEntity.ok(MediaFileDto.of(mediaService.save(file, fileTypeEnum)));
  }

  @GetMapping("/{fileId}")
  public ResponseEntity<MediaFileDto> get(
      @PathVariable String fileId,
      @UserId String userId
  ) {

    return ResponseEntity.ok(MediaFileDto.of(mediaService.get(fileId)));
  }

}
