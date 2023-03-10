package com.content_i_like.domain.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MemberLoginRequest {

  private String email;
  private String password;
}
