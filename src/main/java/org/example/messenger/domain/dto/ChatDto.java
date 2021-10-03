package org.example.messenger.domain.dto;

import lombok.*;
import org.example.messenger.domain.model.Chat;
import org.example.messenger.domain.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class ChatDto {

  private String id;

  private MessageDto lastMessage;

}
