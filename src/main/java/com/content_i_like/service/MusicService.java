package com.content_i_like.service;

import com.content_i_like.domain.dto.tracks.TrackGetResponse;
import com.content_i_like.domain.entity.Member;
import com.content_i_like.domain.entity.Track;
import com.content_i_like.exception.ContentILikeAppException;
import com.content_i_like.exception.ErrorCode;
import com.content_i_like.repository.MemberRepository;
import com.content_i_like.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MusicService {

  private final TrackRepository trackRepository;
  private final MemberRepository memberRepository;

  public Member validateMember(String userEmail) {
    return memberRepository.findByEmail(userEmail)
        .orElseThrow(() -> new ContentILikeAppException(ErrorCode.NOT_FOUND,
            ErrorCode.NOT_FOUND.getMessage()));
  }

  public Page<TrackGetResponse> getEveryTrack(Pageable pageable, String userEmail) {
    Member member = validateMember(userEmail);

    Page<Track> tracks = trackRepository.findAll(pageable);

    return tracks.map(TrackGetResponse::of);
  }
}
