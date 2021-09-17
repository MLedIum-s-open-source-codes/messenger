package org.example.messenger.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chats_users")
public class ChatUser {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "chat_id",
      referencedColumnName = "id")
  private Chat chat;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id",
      referencedColumnName = "id")
  private User user;

}
