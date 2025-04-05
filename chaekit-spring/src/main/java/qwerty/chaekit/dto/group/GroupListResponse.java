package qwerty.chaekit.dto.group;

import lombok.Builder;

import java.util.List;

@Builder
public record GroupListResponse (
        List<GroupFetchResponse> groups,
        Integer currentPage,
        Long totalItems,
        Integer totalPages
) { }
