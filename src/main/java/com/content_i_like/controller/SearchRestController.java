package com.content_i_like.controller;

import com.content_i_like.domain.Response;
import com.content_i_like.domain.dto.tracks.TrackPageGetResponse;
import com.content_i_like.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchRestController {

    private final SearchService searchService;

    @GetMapping("/search")
    public Response<TrackPageGetResponse> showAllTracks(final Authentication authentication,
                                                          @PageableDefault(sort = "trackNo", direction = Sort.Direction.DESC) Pageable pageable) {

        TrackPageGetResponse searchResults = searchService.getEveryTrack(pageable, authentication.getName());

        return Response.success(searchResults);
    }

    @GetMapping("/search/{trackTitle}")
    public Response<TrackPageGetResponse> searchByKeyword(final Authentication authentication,
                                                          @PathVariable String trackTitle,
                                                          @PageableDefault(sort = "trackNo", direction = Sort.Direction.DESC) Pageable pageable) {

        TrackPageGetResponse searchResults =
                searchService.findTracksWithKeyword(pageable, trackTitle, authentication.getName());

        return Response.success(searchResults);
    }

}