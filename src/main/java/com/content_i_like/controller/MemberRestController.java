package com.content_i_like.controller;

import com.content_i_like.domain.Response;
import com.content_i_like.domain.dto.member.*;
import com.content_i_like.domain.entity.Member;
import com.content_i_like.domain.entity.Point;
import com.content_i_like.service.MailService;
import com.content_i_like.service.MemberService;
import com.content_i_like.service.PointService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class MemberRestController {

  private final MemberService memberService;

  @PostMapping("/join")
  public Response<MemberJoinResponse> join(
      @RequestPart(value = "dto") @Valid final MemberJoinRequest request,
      @RequestPart(value = "file", required = false) MultipartFile file) {
    MemberJoinResponse response = memberService.join(request);
    return Response.success(response);
  }

  @PostMapping("/login")
  public Response<MemberLoginResponse> login(@RequestBody @Valid final MemberLoginRequest request) {
    MemberLoginResponse response = memberService.login(request);
    return Response.success(response);
  }

  @PostMapping("/passwd/find_pw")
  public Response<String> findPw(@RequestBody @Valid final MemberFindRequest request) {
    String message = memberService.findPwByEmail(request);
    return Response.success(message);
  }

  @PostMapping("/passwd/change")
  public Response<String> changePw(@RequestBody @Valid final ChangePwRequest request,
      final Authentication authentication) {
    return Response.success(memberService.changePw(request, authentication.getName()));
  }

  @GetMapping("/my")
  public Response<MemberResponse> getMyInfo(final Authentication authentication) {
    MemberResponse memberResponse = memberService.getMyInfo(authentication.getName());
    return Response.success(memberResponse);
  }

  @GetMapping("/my/point")
  public Response<MemberPointResponse> getMyPoint(final Authentication authentication) {
    MemberPointResponse memberPointResponse = memberService.getMyPoint(authentication.getName());
    return Response.success(memberPointResponse);
  }

  @PutMapping("/my")
  public Response<MemberResponse> modifyMyInfo(
      @RequestPart(value = "dto") @Valid final MemberModifyRequest request,
      @RequestPart(value = "file", required = false) MultipartFile file,
      final Authentication authentication) throws IOException {
    MemberResponse memberResponse = memberService
        .modifyMyInfo(request, file, authentication.getName());
    return Response.success(memberResponse);
  }

  @PutMapping("/my/profileImg")
  public Response<String> updateProfileImg(@RequestPart(value = "file") MultipartFile file,
      Authentication authentication) throws IOException {
    String url = memberService.uploadProfileImg(authentication.getName(), file);
    return Response.success(url);
  }

}
