package org.example.messenger.domain.response;

import lombok.Builder;
import lombok.Getter;
import org.example.messenger.domain.dto.MessageDto;
import org.example.messenger.domain.dto.UserDto;

import java.util.List;

@Getter
@Builder
public class ChatHistory {

  private final UserDto me;

  private final List<UserDto> usersData;

  private final List<MessageDto> messages;

}
