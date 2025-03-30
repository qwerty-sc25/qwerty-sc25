package qwerty.chaekit.dto;

import lombok.Builder;

@Builder
public record UserMemberResponse(
        Long id,
        String nickname,
        String username,
        String role
){ }
