package org.example.messenger.entity;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chats")
public class Chat {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Builder.Default
  @OneToMany(mappedBy = "chat",
      fetch = FetchType.EAGER,
      cascade = CascadeType.REMOVE,
      orphanRemoval = true)
  @EqualsAndHashCode.Exclude
  private Set<ChatUser> chatUsers = new HashSet<>();

  @Builder.Default
  @OneToMany(mappedBy = "chat",
      fetch = FetchType.EAGER,
      cascade = CascadeType.REMOVE,
      orphanRemoval = true)
  @EqualsAndHashCode.Exclude
  private Set<Message> messages = new HashSet<>();

}
