package qwerty.chaekit.dto;

import lombok.Builder;

@Builder
public record PublisherMemberResponse(
        Long id,
        String publisherName,
        String username,
        String role,
        Boolean isAccepted
){ }
