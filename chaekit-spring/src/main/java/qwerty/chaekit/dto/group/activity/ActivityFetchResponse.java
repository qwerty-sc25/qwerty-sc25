package qwerty.chaekit.dto.group.activity;

import lombok.Builder;
import qwerty.chaekit.domain.group.activity.Activity;

import java.time.LocalDate;

@Builder
public record ActivityFetchResponse(
        Long activityId,
        Long groupId,
        Long bookId,
        String bookTitle,
        String bookAuthor,
        String coverImageURL,
        String bookDescription,
        LocalDate startTime,
        LocalDate endTime,
        String description,
        boolean isParticipant,
        Long highlightCount,
        Long discussionCount
) {
    public static ActivityFetchResponse of(Activity activity, String coverImageURL, boolean isParticipant, Long highlightCount, Long discussionCount) {
        return ActivityFetchResponse.builder()
                .groupId(activity.getGroup().getId())
                .activityId(activity.getId())
                .bookId(activity.getBook().getId())
                .bookTitle(activity.getBook().getTitle())
                .bookAuthor(activity.getBook().getAuthor())
                .coverImageURL(coverImageURL)
                .bookDescription(activity.getBook().getDescription())
                .startTime(activity.getStartTime())
                .endTime(activity.getEndTime())
                .description(activity.getDescription())
                .isParticipant(isParticipant)
                .highlightCount(highlightCount)
                .discussionCount(discussionCount)
                .build();
    }
}
