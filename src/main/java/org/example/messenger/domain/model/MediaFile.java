package org.example.messenger.domain.model;

import lombok.*;
import org.example.messenger.domain.audit.BaseModel;
import org.example.messenger.enumeration.MediaFileTypeEnum;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "media_files")
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class MediaFile extends BaseModel {

  @Id
  private String id;

  private String title;

  private String url;

  private MediaFileTypeEnum type;

  private String fileNameInStorage;

}
