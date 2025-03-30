package qwerty.chaekit.global.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityRequestReader {
    private final ObjectMapper objectMapper;

    public <T> T read(HttpServletRequest request, Class<T> clazz) {
        try {
            return objectMapper.readValue(request.getInputStream(), clazz);
        } catch (Exception e) {
            throw new RuntimeException("요청 파싱 실패: " + clazz.getSimpleName(), e);
        }
    }
}
