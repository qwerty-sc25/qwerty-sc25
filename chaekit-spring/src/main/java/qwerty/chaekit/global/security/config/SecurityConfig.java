package qwerty.chaekit.global.security.config;

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
import qwerty.chaekit.domain.Member.enums.Role;
import qwerty.chaekit.global.security.filter.CustomExceptionHandlingFilter;
import qwerty.chaekit.global.security.filter.JwtFilter;
import qwerty.chaekit.global.jwt.JwtUtil;
import qwerty.chaekit.global.security.filter.login.LoginFilter;
import qwerty.chaekit.global.security.handler.CustomAccessDeniedHandler;
import qwerty.chaekit.global.security.handler.CustomAuthenticationEntryPoint;
import qwerty.chaekit.global.util.SecurityRequestReader;
import qwerty.chaekit.global.util.SecurityResponseSender;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
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

        http
                .formLogin(AbstractHttpConfigurer::disable);

        http
                .httpBasic(AbstractHttpConfigurer::disable);

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api", "/api/users/join").permitAll()
                        .requestMatchers("/api/users/**").hasAuthority(Role.ROLE_USER.name())

                        .requestMatchers("/api/publishers/join").permitAll()
                        .requestMatchers("/api/publishers/**").hasAuthority(Role.ROLE_PUBLISHER.name())

                        .requestMatchers("/admin/**").hasAuthority(Role.ROLE_ADMIN.name())
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
