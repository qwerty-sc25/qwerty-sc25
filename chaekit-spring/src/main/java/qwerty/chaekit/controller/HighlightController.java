package qwerty.chaekit.controller;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import qwerty.chaekit.dto.highlight.HighlightListResponse;
import qwerty.chaekit.dto.highlight.HighlightPostRequest;
import qwerty.chaekit.dto.highlight.HighlightPostResponse;
import qwerty.chaekit.dto.highlight.HighlightPutRequest;
import qwerty.chaekit.global.response.ApiSuccessResponse;
import qwerty.chaekit.global.security.resolver.Login;
import qwerty.chaekit.global.security.resolver.LoginMember;
import qwerty.chaekit.service.HighlightService;

@RestController
@RequestMapping("/api/highlights")
@RequiredArgsConstructor
public class HighlightController {
    private final HighlightService highlightService;

    @GetMapping
    public ApiSuccessResponse<HighlightListResponse> getHighlights(@Login LoginMember loginMember,
                                               @ParameterObject Pageable pageable,
                                               @RequestParam(required = false) Long activityId,
                                               @RequestParam(required = false) Long bookId,
                                               @RequestParam(required = false) String spine,
                                               @RequestParam(required = false) Boolean me
    ) {
        return ApiSuccessResponse.of(highlightService.fetchHighlights(loginMember, pageable, activityId, bookId, spine, me));
    }

    @PostMapping
    public ApiSuccessResponse<HighlightPostResponse> createHighlight(@Login LoginMember loginMember, @RequestBody HighlightPostRequest request) {
        return ApiSuccessResponse.of(highlightService.createHighlight(loginMember, request));
    }

    @PutMapping("/{id}")
    public ApiSuccessResponse<HighlightPostResponse> updateHighlight(@Login LoginMember loginMember, @PathVariable Long id, @RequestBody HighlightPutRequest request) {
        return ApiSuccessResponse.of(highlightService.updateHighlight(loginMember, id, request));
    }
}
