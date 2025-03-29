package qwerty.chaekit.dto;

import lombok.Builder;

@Builder
public record PublisherJoinResponse(
        Long id,
        String accessToken,
        String publisherName,
        String username,
        String role,
        Boolean isAccepted
){ }
