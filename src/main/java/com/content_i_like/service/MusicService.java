package com.content_i_like.service;

import static com.content_i_like.service.validchecks.ArbitraryValidationService.validate;

import com.content_i_like.domain.dto.comment.CommentReadResponse;
import com.content_i_like.domain.dto.recommend.RecommendReadResponse;
import com.content_i_like.domain.dto.search.SearchRecommendsResponse;
import com.content_i_like.domain.dto.tracks.TrackGetResponse;
import com.content_i_like.domain.entity.Member;
import com.content_i_like.domain.entity.Recommend;
import com.content_i_like.domain.entity.Track;
import com.content_i_like.exception.ContentILikeAppException;
import com.content_i_like.exception.ErrorCode;
import com.content_i_like.repository.CommentRepository;
import com.content_i_like.repository.MemberRepository;
import com.content_i_like.repository.RecommendRepository;
import com.content_i_like.repository.TrackRepository;
import com.content_i_like.service.validchecks.MemberValidation;
import com.content_i_like.service.validchecks.TrackValidation;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MusicService {

  private final TrackRepository trackRepository;
  private final MemberRepository memberRepository;
  private final RecommendRepository recommendRepository;

  public TrackGetResponse getASingleTrackInfo(Long trackNo, String memberEmail) {
    Member member = validate(memberEmail, new MemberValidation(memberRepository));

    Track track = validate(String.valueOf(trackNo), new TrackValidation(trackRepository));

    List<SearchRecommendsResponse> recommends = getEveryRecommendByTrack(track);

    return TrackGetResponse.of(track, recommends);
  }

  private List<SearchRecommendsResponse> getEveryRecommendByTrack(Track track) {
    List<Recommend> recommends = recommendRepository.findAllByTrack(track)
        .orElseThrow(()->new ContentILikeAppException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage()));

    return recommends.stream().map(recommend ->
        recommend.getRecommendContent().length() < 50
            ? SearchRecommendsResponse.of(recommend, recommend.getRecommendContent())
            : SearchRecommendsResponse.of(recommend, recommend.getRecommendContent().substring(0, 50)))
        .collect(
        Collectors.toList());
  }

  /* ????????? ???????????? ???????????? ?????? ????????? ????????? ??? ?????? ?????? ????????? ?????? ?????? ?????? ??????*/
  public CommentReadResponse bestCommentRecommendOfTrack(List<SearchRecommendsResponse> recommends) {
    CommentReadResponse maxLikedComment = CommentReadResponse.builder().commentPoint(0L).build();

    for (SearchRecommendsResponse recommend : recommends) {
      CommentReadResponse currentMaxLikedComment = recommend.getComments().stream()
          .reduce(CommentReadResponse.builder().commentPoint(0L).build(), CommentReadResponse::commentWithMaxPoints);

      maxLikedComment = CommentReadResponse.commentWithMaxPoints(maxLikedComment, currentMaxLikedComment);
    }

    return maxLikedComment;
  }
}
