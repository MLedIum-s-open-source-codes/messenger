package org.example.messenger.service;

import org.example.messenger.domain.dto.MessageDto;
import org.example.messenger.domain.model.Message;

import java.util.List;

public interface MessageService {

  Message get(String id);

  Message send(String from, String to, MessageDto dto);

  Message update(Message message);

}
