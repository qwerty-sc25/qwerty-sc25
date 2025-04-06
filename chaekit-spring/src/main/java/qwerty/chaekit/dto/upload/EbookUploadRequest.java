package qwerty.chaekit.dto.upload;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

public record EbookUploadRequest(
        @Schema(description = "책 제목", example = "이상한 나라의 앨리스")
        String title,

        @Schema(description = "책 저자", example = "루이스 캐럴")
        String author,

        @Schema(description = "책 설명", example = "《이상한 나라의 앨리스》는 영국의 수학자이자 작가인 찰스 루트위지 도지슨이 루이스 캐럴이라는 필명으로 1865년에 발표한 소설이다.")
        String description,

        @Schema(description = "책 파일", example = "Alice.epub", type = "string", format = "binary")
        MultipartFile file
) {
}
