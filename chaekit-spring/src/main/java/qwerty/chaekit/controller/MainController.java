package qwerty.chaekit.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
    @GetMapping("/")
    public String mainApi() {
        return "Welcome to Chaekit";
    }
}
