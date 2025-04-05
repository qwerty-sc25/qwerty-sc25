package qwerty.chaekit.dto;

import lombok.Builder;

@Builder
public record LoginResponse(
        Long id,
        String role,
        String accessToken
){ }
