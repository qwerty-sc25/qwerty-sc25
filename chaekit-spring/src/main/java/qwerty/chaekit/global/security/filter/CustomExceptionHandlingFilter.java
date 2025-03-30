package qwerty.chaekit.global.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.filter.OncePerRequestFilter;
import qwerty.chaekit.global.util.SecurityResponseSender;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class CustomExceptionHandlingFilter extends OncePerRequestFilter {
    private final SecurityResponseSender responseSender;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (AuthenticationException | AccessDeniedException e) {
            throw e;
        } catch (RuntimeException e) {
            log.error("Unhandled exception occurred:", e);
            responseSender.sendError(response, 500, "INTERNAL_ERROR", "서버 내부 오류가 발생했습니다.");
        }
    }
}