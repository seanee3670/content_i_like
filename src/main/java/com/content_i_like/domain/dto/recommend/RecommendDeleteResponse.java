package com.content_i_like.domain.dto.recommend;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RecommendDeleteResponse {

  private Long recommendNo;
  private String message;
}
