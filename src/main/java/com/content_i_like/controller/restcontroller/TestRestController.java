package com.content_i_like.controller.restcontroller;

import com.content_i_like.domain.enums.TrackEnum;
import com.content_i_like.service.NewTrackService;
import com.content_i_like.service.TrackService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
@Slf4j
public class TestRestController {

  private final TrackService trackService;
  private final NewTrackService newTrackService;

  @Value("${spotify.client.id}")
  private String CLIENT_ID;

  @GetMapping("/tracks")
  public String getTracks(@RequestParam String token) throws IOException {
    trackService.createAllThreeTypesDB(token);

    return "DB 저장이 완료되었습니다.";
  }

  @GetMapping("/new/tracks")
  public String getNewTracks(@RequestParam String token) throws IOException {
    newTrackService.createAllThreeTypesDBAllUnique(token);

    return "새로운 DB 저장이 완료되었습니다.";
  }

  @GetMapping("/token")
  public ResponseEntity<?> requirePermission() {
    HttpHeaders headers = new HttpHeaders();

    String uri = "https://accounts.spotify.com/authorize?"
        + String.format("client_id=%s&response_type=%s&redirect_uri=%s", CLIENT_ID,
        "code", TrackEnum.REDIRECT_URI.getValue());

    headers.setLocation(URI.create(uri));

    return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
  }

  @GetMapping("")
  public ResponseEntity<?> getAccessToken(@RequestParam String code)
      throws JsonProcessingException {
    HttpHeaders headers = new HttpHeaders();

    String accessToken = trackService.spotifyAccessTokenGenerator(code);

    String uri = "http://localhost:8080/api/v1/test/new/tracks?token=" + accessToken;

    headers.setLocation(URI.create(uri));

    return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
  }


}