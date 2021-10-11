package org.example.messenger.domain.response;

import lombok.Builder;
import lombok.Getter;
import org.example.messenger.domain.dto.MediaFileDto;
import org.example.messenger.domain.dto.MessageDto;
import org.example.messenger.domain.dto.UserDto;
import org.example.messenger.enumeration.ChatTypeEnum;

import java.util.List;

@Getter
@Builder
public class ChatHistory {

  private final UserDto me;

  private final MediaFileDto photo;

  private final ChatTypeEnum chatType;

  private final List<UserDto> usersData;

  private final List<MessageDto> messages;

}
