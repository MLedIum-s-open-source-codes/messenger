package org.example.messenger.service.impl.chat;

import org.example.messenger.domain.model.Chat;
import org.example.messenger.domain.model.ChatUser;
import org.example.messenger.enumeration.ChatTypeEnum;
import org.example.messenger.repository.ChatRepository;
import org.example.messenger.service.DirectChatService;
import org.example.messenger.service.MediaService;
import org.example.messenger.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Qualifier(value = "directChatService")
public class DirectChatServiceImpl extends ChatServiceImpl implements DirectChatService {

  private final ChatRepository chatRepository;
  private final UserService userService;

  @Autowired
  public DirectChatServiceImpl(ChatRepository chatRepository, UserService userService, MediaService mediaService) {
    super(chatRepository);
    this.chatRepository = chatRepository;
    this.userService = userService;
  }

  @Override
  public Chat getOrCreate(String userId, String interlocutorId) {
    Chat chat = getByUsersIds(userId, interlocutorId);
    if (chat == null) chat = create(userId, interlocutorId);

    return chat;
  }

  @Override
  public Chat getOrCreate(String userId) {
    Chat chat = getByUsersIds(userId);
    if (chat == null) chat = create(userId);

    return chat;
  }

  private Chat create(String userId, String interlocutorId) {
    if (userId.equals(interlocutorId))
      return create(userId);

    userService.checkExistsUserWithId(userId);
    userService.checkExistsUserWithId(interlocutorId);

    ChatUser chatUser1 = ChatUser.builder().userId(userId).build();
    ChatUser chatUser2 = ChatUser.builder().userId(interlocutorId).build();

    Chat chat = Chat.builder()
        .user(chatUser1)
        .user(chatUser2)
        .type(ChatTypeEnum.DIRECT_MESSAGE)
        .build();

    return update(chat);
  }

  private Chat create(String userId) {
    userService.checkExistsUserWithId(userId);

    ChatUser chatUser1 = ChatUser.builder().userId(userId).build();

    Chat chat = Chat.builder().user(chatUser1).type(ChatTypeEnum.FAVOURITES).build();

    return update(chat);
  }

  @Override
  public Chat getByUsersIds(String userId, String interlocutorId) {
    Optional<Chat> optional = chatRepository.findByUsersIds(userId, interlocutorId, ChatTypeEnum.DIRECT_MESSAGE.name());
    if (optional.isEmpty())
      return null;

    return optional.get();
  }

  @Override
  public Chat getByUsersIds(String userId) {
    Optional<Chat> optional = chatRepository.findByUserId(userId, ChatTypeEnum.FAVOURITES.name());
    if (optional.isEmpty())
      return null;

    return optional.get();
  }


}
