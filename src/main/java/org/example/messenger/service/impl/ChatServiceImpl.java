package org.example.messenger.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.messenger.domain.model.Chat;
import org.example.messenger.domain.model.ChatUser;
import org.example.messenger.domain.model.MediaFile;
import org.example.messenger.domain.model.User;
import org.example.messenger.domain.request.CreateConversationRequest;
import org.example.messenger.enumeration.ChatTypeEnum;
import org.example.messenger.enumeration.ErrorTypeEnum;
import org.example.messenger.exception.CustomException;
import org.example.messenger.repository.ChatRepository;
import org.example.messenger.service.ChatService;
import org.example.messenger.service.MediaService;
import org.example.messenger.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

  private final ChatRepository chatRepository;
  private final UserService userService;
  private final MediaService mediaService;

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

  @Override
  public Chat createConversation(String creatorId, CreateConversationRequest createConversationRequest) {
    User creator = userService.get(creatorId);
    List<User> users = createConversationRequest.getUsers().stream().map(
        userDto -> userService.get(userDto.getId())
    ).collect(Collectors.toList());

    List<ChatUser> chatUsers = users.stream().map(
        user -> new ChatUser(user.getId())
    ).collect(Collectors.toList());
    chatUsers.add(new ChatUser(creator.getId(), true));

    MediaFile photo = createConversationRequest.getPhoto() == null ? null : mediaService.get(createConversationRequest.getPhoto().getId());

    Chat chat = update(Chat.builder()
        .users(chatUsers)
        .photo(photo)
        .type(ChatTypeEnum.CONVERSATION)
        .build());

    users.add(creator);
    users.forEach(user -> {
      user.addConversation(chat);
      userService.update(user);
    });

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
  public Chat getById(String id) {
    Optional<Chat> optional = chatRepository.findById(id);
    if (optional.isEmpty())
      throw new CustomException(ErrorTypeEnum.NOT_FOUND, "Chat was not found");

    return optional.get();
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

  @Override
  public List<Chat> getAllByUserId(String userId) {

    return chatRepository.findAllByUserId(userId);
  }

  @Override
  public Chat update(Chat chat) {

    return chatRepository.save(chat);
  }

}
