package com.content_i_like.controller.restcontroller;

import com.content_i_like.domain.Response;
import com.content_i_like.domain.dto.faq.FaqDetailsResponse;
import com.content_i_like.domain.dto.faq.FaqRequest;
import com.content_i_like.domain.dto.faq.FaqResponse;
import com.content_i_like.service.FaqService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/faq")
public class FaqRestController {

  private final FaqService faqService;

  @GetMapping()
  public Response<Page<FaqResponse>> getAllFaq(Pageable pageable) {
    return Response.success(faqService.getAllFaq(pageable));
  }

  @GetMapping("/{category}")
  public Response<Page<FaqResponse>> getFaqByCategory(Pageable pageable,
      @PathVariable String category) {
    return Response.success(faqService.getFaqByCategory(pageable, category));
  }

  @GetMapping("/searches/{keyWord}")
  public Response<Page<FaqResponse>> getFaqByKeyWord(Pageable pageable,
      @PathVariable String keyWord) {
    return Response.success(faqService.getFaqByKeyWord(pageable, keyWord));
  }

  @GetMapping("/details/{faqNo}")
  public Response<FaqDetailsResponse> getFaqDetails(@PathVariable Long faqNo) {
    return Response.success(faqService.getFaqDetails(faqNo));
  }

  @PostMapping()
  public Response<FaqResponse> addFaq(@RequestBody FaqRequest faqRequest) {
    return Response.success(faqService.addFaq(faqRequest));
  }
}
