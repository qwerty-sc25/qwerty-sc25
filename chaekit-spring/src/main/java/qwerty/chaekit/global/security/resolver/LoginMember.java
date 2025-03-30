package qwerty.chaekit.global.security.resolver;

import lombok.Builder;

@Builder
public record LoginMember(
        Long memberId,
        String username,
        String role
) {
}