package org.example.messenger.domain.response;

import lombok.Builder;
import lombok.Getter;
import org.example.messenger.domain.dto.ChatDto;
import org.example.messenger.domain.dto.UserDto;

import java.util.List;

@Getter
@Builder
public class DialogsPreload {

  private final UserDto me;

  private final List<UserDto> usersData;

  private final List<ChatDto> chatsPreload;

}
