package org.example.messenger.service;

import org.example.messenger.domain.dto.MessageDto;
import org.example.messenger.entity.Message;

public interface MessageService {

  Message create(MessageDto dto);

  Message get(Long id);

  Message edit(MessageDto dto);

  Message update(Message message);

  void delete(Long id);

}
