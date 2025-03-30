package qwerty.chaekit.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import qwerty.chaekit.dto.UserJoinRequest;
import qwerty.chaekit.dto.UserJoinResponse;
import qwerty.chaekit.dto.UserMemberResponse;
import qwerty.chaekit.global.security.resolver.Login;
import qwerty.chaekit.global.security.resolver.LoginMember;
import qwerty.chaekit.service.UserJoinService;
import qwerty.chaekit.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserJoinService joinService;
    private final UserService userService;

    @GetMapping("/me")
    public UserMemberResponse userInfo(@Login LoginMember loginMember) {
        return userService.getUserProfile(loginMember);
    }

    @PostMapping("/join")
    public UserJoinResponse userJoin(@RequestBody @Valid UserJoinRequest joinRequest) {
        return joinService.join(joinRequest);
    }
}
