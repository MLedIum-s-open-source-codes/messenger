package org.example.messenger.domain.dto;

import lombok.*;
import org.example.messenger.domain.model.MediaFile;
import org.example.messenger.enumeration.MediaFileTypeEnum;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class MediaFileDto {

  private String id;
  private String title;
  private String url;
  private MediaFileTypeEnum type;

  public static MediaFileDto of(MediaFile mediaFile) {
    return MediaFileDto.builder()
        .id(mediaFile.getId())
        .title(mediaFile.getTitle())
        .url(mediaFile.getUrl())
        .type(mediaFile.getType())
        .build();
  }

}
