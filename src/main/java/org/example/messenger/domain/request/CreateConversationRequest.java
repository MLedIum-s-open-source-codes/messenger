package org.example.messenger.domain.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.messenger.domain.dto.MediaFileDto;
import org.example.messenger.domain.dto.UserDto;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateConversationRequest {

  private List<UserDto> users;

  private String title;

  private MediaFileDto photo;

}
