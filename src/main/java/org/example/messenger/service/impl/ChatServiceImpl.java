package org.example.messenger.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.messenger.domain.model.Chat;
import org.example.messenger.domain.model.ChatUser;
import org.example.messenger.repository.ChatRepository;
import org.example.messenger.service.ChatService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

  private final ChatRepository chatRepository;

  @Override
  public Chat getOrCreateChat(String userId, String interlocutorId) {
    Optional<Chat> optional = getChat(userId, interlocutorId);

    if (optional.isEmpty())
        return createChat(userId, interlocutorId);

    return optional.get();
  }

  private Chat createChat(String userId, String interlocutorId) {
    ChatUser chatUser1 = ChatUser.builder().userId(userId).build();
    ChatUser chatUser2 = ChatUser.builder().userId(interlocutorId).build();

    Chat chat = Chat.builder()
        .user(chatUser1)
        .user(chatUser2)
        .build();

    return update(chat);
  }

  private Optional<Chat> getChat(String id1, String id2) {

    return chatRepository.findByUsersIds(id1, id2);
  }

  @Override
  public List<Chat> getChatsByUserId(String userId) {
    List<Chat> chats = chatRepository.findAllByUserId(userId);

    chats.forEach(chat ->
        chat.setUsers(
            chat.getUsers().stream().filter(strUser -> !strUser.getUserId().equals(userId)).collect(Collectors.toList())
        )
    );

    return chats;
  }

  @Override
  public Chat update(Chat chat) {

    return chatRepository.save(chat);
  }

}
