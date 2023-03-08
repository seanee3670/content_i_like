package com.content_i_like.repository;

import com.content_i_like.domain.entity.Track;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackRepository extends JpaRepository<Track, Long> {
  Optional<Track> findByTrackTitle(String trackTitle);

  Optional<Page<Track>> findAllByTrackTitleContainingOrAlbumAlbumTitleContainingOrArtistArtistNameContaining
      (String trackTitle, String albumTitle, String artistName, Pageable pageable);
}
