package org.example.messenger.service.impl.chat;

import org.example.messenger.domain.model.Chat;
import org.example.messenger.domain.model.ChatUser;
import org.example.messenger.domain.model.MediaFile;
import org.example.messenger.domain.model.User;
import org.example.messenger.domain.request.CreateConversationRequest;
import org.example.messenger.enumeration.ChatTypeEnum;
import org.example.messenger.repository.ChatRepository;
import org.example.messenger.service.ConversationChatService;
import org.example.messenger.service.MediaService;
import org.example.messenger.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Qualifier(value = "conversationChatService")
public class ConversationChatServiceImpl extends ChatServiceImpl implements ConversationChatService {

  private final UserService userService;
  private final MediaService mediaService;

  @Autowired
  public ConversationChatServiceImpl(ChatRepository chatRepository, UserService userService, MediaService mediaService) {
    super(chatRepository);
    this.userService = userService;
    this.mediaService = mediaService;
  }

  @Override
  public Chat createConversation(String creatorId, CreateConversationRequest createConversationRequest) {
    User creator = userService.get(creatorId);
    List<User> users = createConversationRequest.getUsers().stream().map(
        userDto -> userService.get(userDto.getId())
    ).collect(Collectors.toList());

    List<ChatUser> chatUsers = users.stream().map(
        user -> new ChatUser(user.getId(), creatorId)
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

  @Override
  public void addUserToConversation(String chatId, String userId, String addingUserId) {
    Chat chat = getById(chatId);
    chat.addUser(userId, addingUserId);
    update(chat);
  }

  @Override
  public void removeUserFromConversation(String chatId, String userId, String removableUserId) {
    Chat chat = getById(chatId);
    chat.removeUser(userId, removableUserId);
    update(chat);
  }

}
