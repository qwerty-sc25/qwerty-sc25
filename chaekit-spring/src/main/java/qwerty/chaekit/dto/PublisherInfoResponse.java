package qwerty.chaekit.dto;

import lombok.Builder;
import qwerty.chaekit.domain.member.publisher.PublisherProfile;

import java.time.LocalDateTime;

@Builder
public record PublisherInfoResponse(
        Long id,
        String publisherName,
        LocalDateTime createdAt
) {
    public static PublisherInfoResponse of(PublisherProfile profile) {
        return PublisherInfoResponse.builder()
                .id(profile.getMember().getId())
                .publisherName(profile.getPublisherName())
                .createdAt(profile.getCreatedAt())
                .build();
    }
}