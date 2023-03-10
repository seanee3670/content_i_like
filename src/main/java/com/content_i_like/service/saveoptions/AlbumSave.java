package com.content_i_like.service.saveoptions;

import com.content_i_like.domain.entity.Album;
import com.content_i_like.repository.AlbumRepository;
import com.content_i_like.service.saveoptions.DBSaveOption;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class AlbumSave implements DBSaveOption<Album> {

  private final AlbumRepository albumRepository;

  @Override
  public Album saveNewRow(Album album) {
    return albumRepository.save(album);
  }

  @Override
  public List<Album> saveNewRows(List<Album> entities) {
    return albumRepository.saveAll(entities);
  }

  @Override
  public List<Album> fetchEverything() {
    return albumRepository.findAll();
  }
}
