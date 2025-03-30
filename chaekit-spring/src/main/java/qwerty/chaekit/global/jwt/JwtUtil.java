package qwerty.chaekit.global.jwt;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import qwerty.chaekit.global.properties.JwtProperties;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {
    private final SecretKey secretKey;
    private final JwtProperties jwtProperties;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = new SecretKeySpec(
                jwtProperties.secret().getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm()
        );
    }

    public Claims parseJwt(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }

    public Long getMemberId(Claims claims) {
        return claims.get("memberId", Long.class);
    }

    public String getUsername(Claims claims) {
        return claims.get("username", String.class);
    }

    public String getRole(Claims claims) {
        return claims.get("role", String.class);
    }

    public boolean isValidToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.info("token expired");
            return false;
        } catch (Exception e) {
            // 서명 오류, 잘못된 형식, 지원하지 않는 토큰 등
            log.info("token invalid");
            return false;
        }
    }

    public String createJwt(Long memberId, String username, String role) {

        return Jwts.builder()
                .claim("memberId", memberId)
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.expirationMs()))
                .signWith(secretKey)
                .compact();
    }

}
