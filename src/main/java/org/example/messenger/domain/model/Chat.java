package org.example.messenger.domain.model;

import lombok.*;
import org.example.messenger.domain.audit.BaseModel;
import org.example.messenger.enumeration.ChatTypeEnum;
import org.example.messenger.enumeration.ErrorTypeEnum;
import org.example.messenger.exception.CustomException;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chats")
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class Chat extends BaseModel {

  @Id
  private String id;

  @Singular
  private List<ChatUser> users;

  private ChatTypeEnum type;

  @DBRef
  private MediaFile photo;

  @Builder.Default
  private Integer lastSeqId = 0;

  public Optional<ChatUser> getInterlocutor(String currentUserId) {
    for (ChatUser chatUser : getUsers()) {
      if (!chatUser.getUserId().equals(currentUserId)) {
        return Optional.of(chatUser);
      }
    }

    return Optional.empty();
  }

  public void addUser(String idInvitingUser, String idUserBeingAdded) {
    Optional<ChatUser> invitingChatUser = users.stream().filter(chatUser -> chatUser.getUserId().equals(idInvitingUser)).findFirst();
    if (invitingChatUser.isEmpty())
        throw new CustomException(ErrorTypeEnum.ACCESS_DENIED, "User does not have access to this action");

    Optional<ChatUser> chatUserBeingAdded = users.stream().filter(chatUser -> chatUser.getUserId().equals(idUserBeingAdded)).findFirst();
    if (chatUserBeingAdded.isPresent())
        throw new CustomException(ErrorTypeEnum.BAD_REQUEST, "User is already in conversation");

    users.add(new ChatUser(idUserBeingAdded, idInvitingUser));
  }

  public void removeUser(String idDeletingUser , String idUserBeingDeleted) {
    ChatUser deletingChatUser = users.stream().filter(chatUser -> chatUser.getUserId().equals(idDeletingUser)).findFirst()
        .orElseThrow(() -> new CustomException(ErrorTypeEnum.ACCESS_DENIED, "User does not have access to this action"));

    ChatUser chatUserBeingDeleted = users.stream().filter(
        chatUser -> chatUser.getUserId().equals(idUserBeingDeleted)
    ).findFirst().orElseThrow(() -> new CustomException(ErrorTypeEnum.BAD_REQUEST, "User is not in conversation"));

    if (deletingChatUser.getIsOwner() || chatUserBeingDeleted.getInvitedBy().equals(idDeletingUser) || idDeletingUser.equals(idUserBeingDeleted)) {
      users.remove(chatUserBeingDeleted);
    } else {
      throw new CustomException(ErrorTypeEnum.ACCESS_DENIED, "User does not have access to this action");
    }
  }

}
