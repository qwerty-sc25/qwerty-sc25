package qwerty.chaekit.controller.highlight.reaction;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import qwerty.chaekit.dto.highlight.reaction.HighlightReactionRequest;
import qwerty.chaekit.dto.highlight.reaction.HighlightReactionResponse;
import qwerty.chaekit.global.response.ApiSuccessResponse;
import qwerty.chaekit.global.security.resolver.Login;
import qwerty.chaekit.global.security.resolver.UserToken;
import qwerty.chaekit.service.highlight.reaction.HighlightReactionService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/highlights")
public class HighlightReactionController {
    private final HighlightReactionService highlightReactionService;
    
    @PostMapping("/{highlightId}/reactions")
    public ApiSuccessResponse<HighlightReactionResponse> addReaction(
            @Login UserToken userToken,
            @PathVariable Long highlightId,
            @RequestBody HighlightReactionRequest request) {
        return ApiSuccessResponse.of(highlightReactionService.addReaction(userToken, highlightId, request));
    }

    @DeleteMapping("/reactions/{reactionId}")
    public ApiSuccessResponse<String> deleteReaction(
            @Login UserToken userToken,
            @PathVariable Long reactionId) {
        highlightReactionService.deleteReaction(userToken, reactionId);
        return ApiSuccessResponse.of("이모티콘을 삭제했습니다.");
    }
} 