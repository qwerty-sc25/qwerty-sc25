package qwerty.chaekit.dto.highlight;

import lombok.Builder;

@Builder
public record HighlightPutRequest(
        Long activityId,
        String memo
) { }