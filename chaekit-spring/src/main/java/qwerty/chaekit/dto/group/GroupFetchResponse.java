package qwerty.chaekit.dto.group;

import lombok.Builder;
import qwerty.chaekit.domain.group.GroupTag;
import qwerty.chaekit.domain.group.ReadingGroup;

import java.util.List;

@Builder
public record GroupFetchResponse(
        Long groupId,
        String name,
        String description,
        List<String> tags,
        int memberCount
) {
    public static GroupFetchResponse of(ReadingGroup group) {
        return GroupFetchResponse.builder()
                .groupId(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .tags(group.getTags().stream()
                        .map(GroupTag::getTagName)
                        .toList())
                .memberCount(group.getMembers().size())
                .build();
    }
}
