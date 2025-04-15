package qwerty.chaekit.controller.member.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import qwerty.chaekit.dto.member.UserJoinRequest;
import qwerty.chaekit.dto.member.UserJoinResponse;
import qwerty.chaekit.dto.member.UserMemberResponse;
import qwerty.chaekit.global.response.ApiSuccessResponse;
import qwerty.chaekit.global.security.resolver.Login;
import qwerty.chaekit.global.security.resolver.LoginMember;
import qwerty.chaekit.service.member.user.UserJoinService;
import qwerty.chaekit.service.member.user.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserJoinService joinService;
    private final UserService userService;

    @GetMapping("/me")
    public ApiSuccessResponse<UserMemberResponse> userInfo(@Login LoginMember loginMember) {
        return ApiSuccessResponse.of(userService.getUserProfile(loginMember));
    }

    @PostMapping("/join")
    public ApiSuccessResponse<UserJoinResponse> userJoin(@RequestBody @Valid UserJoinRequest joinRequest) {
        return ApiSuccessResponse.of(joinService.join(joinRequest));
    }
}
