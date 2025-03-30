package qwerty.chaekit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import qwerty.chaekit.service.JoinService;

@RestController
@RequiredArgsConstructor
public class JoinController {
    private final JoinService joinService;

    @PostMapping("/join")
    public String joinProcess(@RequestBody JoinRequest joinRequest) {
        joinService.joinProcess(joinRequest);
        return "ok";
    }
}
