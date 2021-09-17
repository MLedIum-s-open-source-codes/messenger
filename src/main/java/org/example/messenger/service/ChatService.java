package org.example.messenger.service;

import org.example.messenger.domain.dto.ChatDto;
import org.example.messenger.entity.Chat;
import org.example.messenger.enumeration.ErrorTypeEnum;
import org.example.messenger.exception.CustomException;

import static java.lang.String.format;

public interface ChatService {

  Chat create(Long... usersIds);

  Chat create(String title, Long... usersIds);

  Chat get(Long id);

  Chat edit(ChatDto dto);

  default void checkExistsChatWithId(Long id) {
    if (!existsChatWithId(id))
        throw new CustomException(ErrorTypeEnum.NOT_FOUND, format("Chat with id '%s' was not found", id));
  }

  boolean existsChatWithId(Long id);

  Chat update(Chat chat);

  void delete(Long id);

}
