package com.content_i_like.service;

import com.content_i_like.domain.entity.Artist;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ArtistFetch implements Fetch<Artist> {

  @Override
  public String extractTitle(JsonNode root, int count) {
    StringBuilder artistNames = new StringBuilder();
    String artistName = "";

    JsonNode artistNode = root.get("tracks").get(count).get("artists");

    int valuesCount = 0;
    if (artistNode.isArray())
      for (JsonNode element : artistNode) {
        valuesCount++;
      }

    for (int i = 0; i < valuesCount; i++) {
      artistName = artistNode.get(i).get("name").asText();
      artistNames.append(artistName);
      if (i != valuesCount-1) artistNames.append(", ");
    }

    return artistNames.toString();
  }
}
