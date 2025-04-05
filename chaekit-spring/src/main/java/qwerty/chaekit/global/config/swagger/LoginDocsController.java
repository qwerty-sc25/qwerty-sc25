package qwerty.chaekit.global.config.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import qwerty.chaekit.dto.LoginRequest;
import qwerty.chaekit.dto.LoginResponse;

@RestController(value = "LoginController")
@RequestMapping("/api/login")
@Tag(name = "login-filter", description = "로그인 API")
public class LoginDocsController {
    @Operation(summary = "로그인", description = "Spring Security가 처리하는 로그인 API")
    @PostMapping
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        // This endpoint is only for Swagger documentation and should not be invoked.
        throw new UnsupportedOperationException("This endpoint is not implemented. It is only for Swagger documentation.");
    }
}