package qwerty.chaekit.global.security.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import qwerty.chaekit.domain.member.enums.Role;
import qwerty.chaekit.global.properties.CorsProperties;
import qwerty.chaekit.global.security.filter.CustomExceptionHandlingFilter;
import qwerty.chaekit.global.security.filter.JwtFilter;
import qwerty.chaekit.global.jwt.JwtUtil;
import qwerty.chaekit.global.security.filter.login.LoginFilter;
import qwerty.chaekit.global.security.handler.CustomAccessDeniedHandler;
import qwerty.chaekit.global.security.handler.CustomAuthenticationEntryPoint;
import qwerty.chaekit.global.util.SecurityRequestReader;
import qwerty.chaekit.global.util.SecurityResponseSender;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CorsProperties corsProperties;

    private final JwtUtil jwtUtil;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final SecurityResponseSender responseSender;
    private final SecurityRequestReader requestReader;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {//비밀번호 안전하게 암호화하는 클래스
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CustomExceptionHandlingFilter customExceptionHandlingFilter() {
        return new CustomExceptionHandlingFilter(responseSender);
    }

    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter(jwtUtil);
    }

    @Bean
    public LoginFilter loginFilter(AuthenticationManager authManager) {
        return new LoginFilter("/api/login", jwtUtil, authManager, requestReader, responseSender);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           LoginFilter loginFilter,
                                           JwtFilter jwtFilter,
                                           CustomExceptionHandlingFilter exceptionHandlingFilter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable);

        http.cors(cors -> cors
                .configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration config = new CorsConfiguration();

                        config.setAllowedOrigins(List.of(corsProperties.allowedOrigins().split(",")));
                        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                        config.setAllowedHeaders(List.of("*"));

                        config.setExposedHeaders(Collections.singletonList("Authorization"));
                        return config;
                    }
                }));

        http
                .formLogin(AbstractHttpConfigurer::disable);

        http
                .httpBasic(AbstractHttpConfigurer::disable);

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/swagger-resources/**", "/webjars/**")
                        .permitAll()
                        .requestMatchers("/api", "/api/users/join").permitAll()
                        .requestMatchers("/api/users/**").hasAuthority(Role.ROLE_USER.name())

                        .requestMatchers("/api/publishers/join").permitAll()
                        .requestMatchers("/api/publishers/**").hasAuthority(Role.ROLE_PUBLISHER.name())

                        .requestMatchers("/api/admin/**").hasAuthority(Role.ROLE_ADMIN.name())
                        .anyRequest().authenticated()
        );

        http
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                );

        http
                .addFilterBefore(exceptionHandlingFilter, SecurityContextHolderFilter.class)
                .addFilterBefore(jwtFilter, LoginFilter.class)
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);

        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
