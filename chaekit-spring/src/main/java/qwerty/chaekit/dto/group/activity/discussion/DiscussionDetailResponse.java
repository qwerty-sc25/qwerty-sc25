package qwerty.chaekit.dto.group.activity.discussion;

import lombok.Builder;
import qwerty.chaekit.dto.highlight.HighlightSummaryResponse;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record DiscussionDetailResponse(
        Long discussionId,
        Long activityId,
        String title,
        String content,
        Long authorId,
        String authorName,
        String authorProfileImage,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt,
        Long commentCount,
        boolean isDebate,
        boolean isAuthor,
        List<HighlightSummaryResponse> linkedHighlights,
        List<DiscussionCommentFetchResponse> comments,
        Long agreeCount,
        Long disagreeCount,
        Long neutralCount
) {
}
