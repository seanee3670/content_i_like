package com.content_i_like.domain.dto.oauth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GoogleUser {
  public String id;
  public String email;
  public Boolean verifiedEmail;
  public String name;
  public String givenName;
  public String familyName;
  public String picture;
  public String locale;
}
