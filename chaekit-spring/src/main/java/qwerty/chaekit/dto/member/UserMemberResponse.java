package qwerty.chaekit.dto.member;

import lombok.Builder;

@Builder
public record UserMemberResponse(
        Long id,
        Long userId,
        String username,
        String nickname
){ }
