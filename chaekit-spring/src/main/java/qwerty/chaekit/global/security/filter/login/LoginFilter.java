package qwerty.chaekit.global.security.filter.login;

import jakarta.annotation.Nullable;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import qwerty.chaekit.domain.member.Member;
import qwerty.chaekit.domain.member.enums.Role;
import qwerty.chaekit.domain.member.publisher.PublisherProfile;
import qwerty.chaekit.domain.member.user.UserProfile;
import qwerty.chaekit.dto.member.LoginRequest;
import qwerty.chaekit.dto.member.LoginResponse;
import qwerty.chaekit.global.jwt.JwtUtil;
import qwerty.chaekit.global.security.model.CustomUserDetails;
import qwerty.chaekit.global.util.SecurityRequestReader;
import qwerty.chaekit.global.util.SecurityResponseSender;
import qwerty.chaekit.service.member.token.RefreshTokenService;
import qwerty.chaekit.service.util.FileService;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final SecurityRequestReader requestReader;
    private final SecurityResponseSender responseSender;
    private final FileService fileService;
    private final RefreshTokenService refreshTokenService;

    public LoginFilter(String loginUrl,
                       JwtUtil jwtUtil,
                       AuthenticationManager authManager,
                       SecurityRequestReader reader,
                       SecurityResponseSender sender,
                       FileService fileService,
                       RefreshTokenService refreshTokenService
    ) {
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authManager;
        this.requestReader = reader;
        this.responseSender = sender;
        this.fileService = fileService;
        this.refreshTokenService = refreshTokenService;

        setAuthenticationManager(authManager);
        setFilterProcessesUrl(loginUrl);
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws AuthenticationException {

        LoginRequest loginRequest = requestReader.read(request, LoginRequest.class);
        String email = loginRequest.email();
        String password = loginRequest.password();
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password, null);

        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain, Authentication authentication
    ) {

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        Member member = customUserDetails.member();
        UserProfile user = customUserDetails.user();
        PublisherProfile publisher = customUserDetails.publisher();
        String profileImageKey;
        Long memberId, userId, publisherId;
        memberId = member.getId();
        if(user != null) {
            profileImageKey = user.getProfileImageKey();
            userId = user.getId();
            publisherId = null;
        } else if(publisher != null){
            profileImageKey = publisher.getProfileImageKey();
            userId = null;
            publisherId = publisher.getId();
        } else {
            userId = null;
            publisherId = null;
            profileImageKey = null;
        }

        String profileImageURL = fileService.convertToPublicImageURL(profileImageKey);
        Role role = customUserDetails.member().getRole();

        String refreshToken = refreshTokenService.issueRefreshToken(memberId);
        String accessToken = jwtUtil.createAccessToken(memberId, userId, publisherId, member.getEmail(), role.name());
        sendSuccessResponse(response, refreshToken, accessToken, member, user, publisher, profileImageURL, role);
    }

    private void sendSuccessResponse(
            HttpServletResponse response,
            String refreshToken,
            String accessToken,
            Member member,
            @Nullable UserProfile user,
            @Nullable PublisherProfile publisher,
            String profileImageURL,
            Role role
    ) {
        LoginResponse loginResponse = LoginResponse.builder()
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .memberId(member.getId())
                .email(member.getEmail())
                .userId(user != null ? user.getId() : null)
                .nickname(user != null ? user.getNickname() : null)
                .publisherId(publisher != null ? publisher.getId() : null)
                .publisherName(publisher != null ? publisher.getPublisherName() : null)
                .profileImageURL(profileImageURL)
                .role(role.name())
                .build();
        responseSender.sendSuccess(response, loginResponse);
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
