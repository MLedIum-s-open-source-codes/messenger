package org.example.messenger.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.messenger.domain.dto.MessageDto;
import org.example.messenger.domain.model.Chat;
import org.example.messenger.domain.model.Message;
import org.example.messenger.domain.model.User;
import org.example.messenger.repository.ChatRepository;
import org.example.messenger.repository.MessageRepository;
import org.example.messenger.service.MessageService;
import org.example.messenger.service.ChatService;
import org.example.messenger.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

  private final ChatRepository chatRepository;
  private final MessageRepository messageRepository;
  private final UserService userService;

  @Override
  public Chat getOrCreateChat(String userId, String interlocutorId) {
    User user = userService.get(userId);
    User interlocutor = userService.get(interlocutorId);

    Chat chat;
    if (chatRepository.existsByUsersContains(user, interlocutor)) {
      chat = getChat(user, interlocutor);
    } else {
      chat = createChat(user, interlocutor);
    }

    if (chat.getMessages() == null) {
      chat.setMessages(new ArrayList<>());
    }

    return chat;
  }

  private Chat createChat(User... users) {
    Chat chat = Chat.builder()
        .users(new ArrayList<>(Arrays.asList(users)))
        .build();

    return update(chat);
  }

  private Chat getChat(User... users) {

    return chatRepository.findByUsersContains(users).get();
  }

  @Override
  public List<Chat> getChatsByUserId(String userId) {
    User user = userService.get(userId);

    List<Chat> chats = chatRepository.findAllByUsersContains(user);

    System.out.println(chats);

    chats.forEach(chat ->
        chat.setUsers(
            chat.getUsers().stream().filter(strUser -> !strUser.getId().equals(userId)).collect(Collectors.toList())
        )
    );

    System.out.println(chats);

    return chats;
  }

  @Override
  public Chat update(Chat chat) {

    return chatRepository.save(chat);
  }

}
