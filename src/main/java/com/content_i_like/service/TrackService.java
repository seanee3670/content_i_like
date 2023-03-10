package com.content_i_like.service;

import com.content_i_like.domain.entity.Album;
import com.content_i_like.domain.entity.Artist;
import com.content_i_like.domain.entity.Track;
import com.content_i_like.domain.enums.TrackEnum;
import com.content_i_like.repository.AlbumRepository;
import com.content_i_like.repository.ArtistRepository;
import com.content_i_like.repository.TrackRepository;
import com.content_i_like.service.fetchoptions.AlbumFetch;
import com.content_i_like.service.fetchoptions.ArtistFetch;
import com.content_i_like.service.fetchoptions.Fetch;
import com.content_i_like.service.fetchoptions.TrackFetch;
import com.content_i_like.service.saveoptions.AlbumSave;
import com.content_i_like.service.saveoptions.ArtistSave;
import com.content_i_like.service.saveoptions.DBSaveOption;
import com.content_i_like.service.saveoptions.TrackSave;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrackService {

  @Value("${spotify.client.id}")
  private String CLIENT_ID;

  @Value("${spotify.client.secret}")
  private String CLIENT_SECRET;

  private final ObjectMapper objectMapper;
  private final TrackRepository trackRepository;
  private final ArtistRepository artistRepository;
  private final AlbumRepository albumRepository;

  public HttpHeaders headerOf(String accessToken) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setBearerAuth(accessToken);
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    return httpHeaders;
  }

  public String spotifyAccessTokenGenerator(String code) throws JsonProcessingException {
    RestTemplate restTemplate = new RestTemplate();

    /* String uri = "https://accounts.spotify.com/api/token"; */
    MultiValueMap<String, String> requiredRequestBody = new LinkedMultiValueMap<>();
    requiredRequestBody.add("grant_type", TrackEnum.GRANT_TYPE.getValue());
    requiredRequestBody.add("code", code);
    requiredRequestBody.add("redirect_uri", TrackEnum.REDIRECT_URI.getValue());

    HttpHeaders httpHeaders = new HttpHeaders();

    String toEncode = CLIENT_ID + ":" + CLIENT_SECRET;

    String authorization
        = Base64.getEncoder().encodeToString(toEncode.getBytes());

    httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    httpHeaders.setBasicAuth(authorization);

    HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(requiredRequestBody,
        httpHeaders);

    ResponseEntity<String> response = restTemplate.postForEntity(TrackEnum.TOKEN_URL.getValue(),
        httpEntity, String.class);

    JsonNode tokenSource = objectMapper.readTree(response.getBody());

    String accessToken = String.valueOf(tokenSource.findValue("access_token"));
    String refreshToken = String.valueOf(tokenSource.findValue("refresh_token"));

    return accessToken.substring(1, accessToken.length() - 1);
  }

  public List<String> collectAllGenres(String filename) throws IOException {
    BufferedReader reader = Files.newBufferedReader(Paths.get(filename));
    List<String> genres = new ArrayList<>();
    String line = "";

    while ((line = reader.readLine()) != null) {
      try {
        genres.add(line);
      } catch (Exception e) {
        log.warn("????????? ???????????? ?????? ????????? ??????????????????.");
      }
    }
    return genres;
  }

  @Transactional
  public List<List<String>> findSpotifyIds(String accessToken) throws IOException {
    RestTemplate restTemplate = new RestTemplate();
    String searchUri = TrackEnum.BASE_URL.getValue() + "/search";
    int limit = 50;

//    List<String> queries =
//        collectAllGenres(
//            "C:\\\\LikeLion\\\\final-project\\\\content_i_like\\\\src\\\\main\\\\extra.csv");

//    List<String> queries = List.of("K-rock", "Classic K-pop", "Korean Soundtrack", "Korean Pop",
//            "Korean Mask Singer", "Korean Traditional", "Korean Phantom Singer", "Korean Instrumental");

    List<String> queries = List.of("Heavy Metal");

    List<List<String>> collectedIds = new ArrayList<>();
    List<String> ids = new ArrayList<>();

    for (Object query : queries) {
      log.info("genre:{}", query);
      for (int offset = 0; offset <= 950; offset += 50) {
        String completeUri =
            searchUri + "?q=genre:" + query + "&type=track&limit=" + limit + "&offset=" + offset
                + "&market=KR";
        // 72??? ????????? ???????????? ?????? ?????? 150?????? ????????????
        ResponseEntity<String> response
            = restTemplate.exchange(completeUri, HttpMethod.GET,
            new HttpEntity<>(headerOf(accessToken)), String.class);

        JsonNode tracksSource = objectMapper.readTree(response.getBody());

        if (tracksSource.at("/tracks/items").isEmpty()) {
          log.info("?????? ????????? ?????? ????????? ????????????!");
          continue;
        }
        for (int i = 0; i < 50; i++) {
          String hrefContainingId = tracksSource.at("/tracks/items/" + i + "/id").asText();

          ids.add(hrefContainingId);
        }
        log.info("ids:{}", ids);
        log.info("size:{}", ids.size());

        List<String> tmpIds = new ArrayList<>(ids);
        ids.clear();

        // ??? ???????????? id 50?????? ???????????? ????????????
        collectedIds.add(tmpIds);
      }
    }
    return collectedIds;
  }

  public List<JsonNode> callTracksApi(String accessToken) throws IOException {
    List<JsonNode> collectedJsonNodes = new ArrayList<>();
    RestTemplate restTemplate = new RestTemplate();
    String trackUri = TrackEnum.BASE_URL.getValue() + "/tracks?ids=";

    List<List<String>> spotifyIds = findSpotifyIds(accessToken);

    for (List<String> spotifyId : spotifyIds) {
      StringBuilder ids = new StringBuilder();
      for (int i = 0; i < spotifyId.size(); i++) {
        ids.append(spotifyId.get(i));
        if (i != 49) {
          ids.append(",");
        }
      }
//      log.info("ids:{}", ids);
      HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(
          headerOf(accessToken));

      ResponseEntity<String> response = restTemplate
          .exchange(trackUri + ids, HttpMethod.GET, httpEntity, String.class);

      /* track uri ???????????? 50?????? ????????? ?????? ??????????????? ???????????? ???????????? ?????? JSON ?????? ????????? ?????? ???????????????,
       * ???????????? ???????????? ????????? ????????? ????????????. ????????? ?????? ????????? ?????? ?????????????????? ???????????? ?????? ????????????. */
      JsonNode infoRoot = objectMapper.readTree(response.getBody());

//      log.info("info:{}", infoRoot.get("tracks").get(0).get("album").get("images").get(1).get("url").asText());

      collectedJsonNodes.add(infoRoot);
    }
    return collectedJsonNodes;
  }

  public <T> List<T> fetchTracks(List<JsonNode> infoRoots, Fetch<T> fetchedType) {
    List<String> titles = new ArrayList<>();
    String data = "";

    for (JsonNode infoRoot : infoRoots) {
      for (int j = 0; j < 50; j++) {
        data = fetchedType.extractData(infoRoot, j);
        if (data.equals("")) {
          continue;
        }
        titles.add(data);
      }
    }
    return fetchedType.parseIntoEntities(titles);
  }

  @Transactional
  public <T> void createMusicDatabase(List<T> entities, DBSaveOption<T> saveOption) {
//    for (T entity : entities) {
//      saveOption.saveNewRow(entity);
//    }
    saveOption.saveNewRows(entities);
  }

  @Transactional
  public void createAllThreeTypesDB(String token) throws IOException {
    /* TODO: ??? ????????? ?????? ????????? ??? ??? ????????? ????????? ?????? ?????? ?????? ?????? */
    List<JsonNode> jsonData = callTracksApi(token);

    List<Artist> artistEntities = fetchTracks(jsonData, new ArtistFetch());
    createMusicDatabase(artistEntities, new ArtistSave(artistRepository));

    List<Album> albumEntities = fetchTracks(jsonData, new AlbumFetch());
    List<Album> albumsAndArtists = parseForAlbum(artistEntities, albumEntities);
    createMusicDatabase(albumsAndArtists, new AlbumSave(albumRepository));

    List<Track> trackEntities = fetchTracks(jsonData, new TrackFetch());
    List<Track> tracksAlbumsAndArtists = parseForTrack(artistEntities, albumEntities,
        trackEntities);
    createMusicDatabase(tracksAlbumsAndArtists, new TrackSave(trackRepository));
  }

  private List<Album> parseForAlbum(List<Artist> artists, List<Album> albums) {
    for (int i = 0; i < albums.size(); i++) {
      albums.get(i).setArtist(artists.get(i));
    }
    return albums;
  }

  private List<Track> parseForTrack(List<Artist> artists, List<Album> albums, List<Track> tracks) {
    for (int i = 0; i < tracks.size(); i++) {
      tracks.get(i).setArtist(artists.get(i));
      tracks.get(i).setAlbum(albums.get(i));
    }
    return tracks;
  }
}