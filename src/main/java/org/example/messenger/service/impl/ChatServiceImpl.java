package org.example.messenger.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.messenger.domain.model.Chat;
import org.example.messenger.domain.model.ChatUser;
import org.example.messenger.repository.ChatRepository;
import org.example.messenger.service.ChatService;
import org.example.messenger.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

  private final ChatRepository chatRepository;
  private final UserService userService;

  @Override
  public Chat getOrCreateChat(String userId, String interlocutorId) {
    Chat chat = getChat(userId, interlocutorId);

    if (chat == null)
      chat = createChat(userId, interlocutorId);

    return chat;
  }

  private Chat createChat(String userId, String interlocutorId) {
    if (userId.equals(interlocutorId))
        return createChat(userId);

    userService.checkExistsUserWithId(userId);
    userService.checkExistsUserWithId(interlocutorId);

    ChatUser chatUser1 = ChatUser.builder().userId(userId).build();
    ChatUser chatUser2 = ChatUser.builder().userId(interlocutorId).build();

    Chat chat = Chat.builder()
        .user(chatUser1)
        .user(chatUser2)
        .build();

    return update(chat);
  }

  private Chat createChat(String userId) {
    userService.checkExistsUserWithId(userId);

    ChatUser chatUser1 = ChatUser.builder().userId(userId).build();

    Chat chat = Chat.builder().user(chatUser1).build();

    return update(chat);
  }

  @Override
  public Chat getChat(String userId, String interlocutorId) {
    if (userId.equals(interlocutorId))
        return getChatByUserId(userId);

    Optional<Chat> optional = chatRepository.findByUsersIds(userId, interlocutorId);
    if (optional.isEmpty())
        return null;

    return optional.get();
  }

  private Chat getChatByUserId(String userId) {
    Optional<Chat> optional = chatRepository.findByUserId(userId);
    if (optional.isEmpty())
        return null;

    return optional.get();
  }

  @Override
  public List<Chat> getChatsByUserId(String userId) {

    return chatRepository.findAllByUserId(userId);
  }

  @Override
  public Chat update(Chat chat) {

    return chatRepository.save(chat);
  }

}
