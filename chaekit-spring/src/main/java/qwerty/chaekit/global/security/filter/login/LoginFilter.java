package qwerty.chaekit.global.security.filter.login;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import qwerty.chaekit.global.security.model.CustomUserDetails;
import qwerty.chaekit.dto.LoginRequest;
import qwerty.chaekit.global.jwt.JwtUtil;
import qwerty.chaekit.global.util.SecurityRequestReader;
import qwerty.chaekit.global.util.SecurityResponseSender;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final SecurityRequestReader requestReader;
    private final SecurityResponseSender responseSender;

    public LoginFilter(String loginUrl,
                       JwtUtil jwtUtil,
                       AuthenticationManager authManager,
                       SecurityRequestReader reader,
                       SecurityResponseSender sender) {
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authManager;
        this.requestReader = reader;
        this.responseSender = sender;

        setAuthenticationManager(authManager);
        setFilterProcessesUrl(loginUrl);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        LoginRequest loginRequest = requestReader.read(request, LoginRequest.class);
        String username = loginRequest.username();
        String password = loginRequest.password();
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authentication) {

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        Long memberId = customUserDetails.getMemberId();
        String username = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        authorities.stream().findFirst().map(GrantedAuthority::getAuthority).ifPresentOrElse(
                (role)-> {
                    String token = jwtUtil.createJwt(memberId, username, role);
                    sendSuccessResponse(response, token, memberId, role);
                }, ()-> responseSender.sendError(response, 500, "INVALID_ROLE", "권한 정보가 존재하지 않습니다.")
        );
    }

    private void sendSuccessResponse(HttpServletResponse response, String token, Long memberId, String role) {
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("accessToken", "Bearer " + token);
        responseData.put("id", memberId);
        responseData.put("role", role);
        responseSender.sendSuccess(response, responseData);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        if (failed instanceof BadCredentialsException) {
            responseSender.sendError(response, 401, "BAD_CREDENTIAL", "아이디 또는 비밀번호가 올바르지 않습니다.");
        } else {
            responseSender.sendError(response, 401, "AUTH_FAILED", failed.getMessage());
        }
    }
}
