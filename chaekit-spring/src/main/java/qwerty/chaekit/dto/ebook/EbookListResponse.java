package qwerty.chaekit.dto.ebook;

import lombok.Builder;
import java.util.List;

@Builder
public record EbookListResponse(
        List<EbookResponse> books,
        Integer currentPage,
        Long totalItems,
        Integer totalPages
) { }
