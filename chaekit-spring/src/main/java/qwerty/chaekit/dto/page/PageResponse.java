package qwerty.chaekit.dto.page;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        Integer currentPage,
        Long totalItems,
        Integer totalPages
) {
}
