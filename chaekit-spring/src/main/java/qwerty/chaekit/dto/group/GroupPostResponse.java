package qwerty.chaekit.dto.group;

import lombok.Builder;
import qwerty.chaekit.domain.group.ReadingGroup;

@Builder
public record GroupPostResponse(
    GroupFetchResponse group
) {
    public static GroupPostResponse of(ReadingGroup group) {
        return new GroupPostResponse(GroupFetchResponse.of(group));
    }
}
