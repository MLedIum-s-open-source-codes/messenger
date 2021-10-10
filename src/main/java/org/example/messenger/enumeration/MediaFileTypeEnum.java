package org.example.messenger.enumeration;

import java.util.Locale;

public enum MediaFileTypeEnum {

  IMAGE,
  VIDEO,
  AUDIO,
  DOCUMENT;

  public static MediaFileTypeEnum ofContentType(String contentType) {
    contentType = contentType.toUpperCase(Locale.ROOT);
    MediaFileTypeEnum[] typeEnums = MediaFileTypeEnum.values();
    for (int i = 0; i < typeEnums.length; i++) {
      if (contentType.startsWith(typeEnums[i].name()))
          return typeEnums[i];
    }

    return DOCUMENT;
  }
}
