package qwerty.chaekit.controller.member.publisher;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import qwerty.chaekit.dto.member.LoginResponse;
import qwerty.chaekit.dto.member.PublisherInfoResponse;
import qwerty.chaekit.dto.member.PublisherJoinRequest;
import qwerty.chaekit.global.response.ApiSuccessResponse;
import qwerty.chaekit.global.security.resolver.Login;
import qwerty.chaekit.global.security.resolver.PublisherToken;
import qwerty.chaekit.service.member.publisher.PublisherJoinService;
import qwerty.chaekit.service.member.publisher.PublisherService;

@RestController
@RequestMapping("/api/publishers")
@RequiredArgsConstructor
@Slf4j
public class PublisherController {
    private final PublisherJoinService joinService;
    private final PublisherService publisherService;

    @GetMapping("/me")
    public ApiSuccessResponse<PublisherInfoResponse> publisherInfo(@Login PublisherToken token) {
        return ApiSuccessResponse.of(publisherService.getPublisherProfile(token));
    }

    @PostMapping(path = "/join", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiSuccessResponse<LoginResponse> publisherJoin(@ModelAttribute @Valid PublisherJoinRequest joinRequest) {
        return ApiSuccessResponse.of(joinService.join(joinRequest));
    }
}
