package qwerty.chaekit.dto.upload;

import lombok.Builder;

@Builder
public record EbookDownloadResponse(
        String presignedUrl
) {
    public static EbookDownloadResponse of(String presignedUrl) {
        return new EbookDownloadResponse(presignedUrl);
    }
}
