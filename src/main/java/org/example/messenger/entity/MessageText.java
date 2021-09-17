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
@Table(name = "messages_text")
public class MessageText {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String text;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "message_id",
      referencedColumnName = "id")
  private Message message;

}
