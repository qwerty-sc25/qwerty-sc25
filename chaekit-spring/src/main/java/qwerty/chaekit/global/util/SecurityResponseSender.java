package qwerty.chaekit.global.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import qwerty.chaekit.global.response.ApiErrorResponse;
import qwerty.chaekit.global.response.ApiSuccessResponse;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityResponseSender {
    private final ObjectMapper objectMapper;

    public void sendError(HttpServletResponse response, int status, String code, String message) {
        sendResponse(response, status, ApiErrorResponse.of(code, message));
    }

    public <T> void sendSuccess(HttpServletResponse response, T data) {
        sendResponse(response, HttpServletResponse.SC_OK, ApiSuccessResponse.of(data));
    }

    private void sendResponse(HttpServletResponse response, int status, Object object) {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        try {
            response.getWriter().write(objectMapper.writeValueAsString(object));
        } catch (IOException e) {
            log.error("Object mapping 실패", e);
        }
    }
}
