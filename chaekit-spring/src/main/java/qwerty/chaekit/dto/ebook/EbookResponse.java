package qwerty.chaekit.dto.ebook;

import lombok.Builder;
import qwerty.chaekit.domain.Member.ebook.Ebook;

@Builder
public record EbookResponse(
        Long id,
        String title,
        String author,
        String description,
        Long size
) {
    public static EbookResponse of(Ebook ebook) {
        return EbookResponse.builder()
                .id(ebook.getId())
                .title(ebook.getTitle())
                .author(ebook.getAuthor())
                .description(ebook.getDescription())
                .size(ebook.getSize())
                .build();
    }
}
