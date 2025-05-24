package qwerty.chaekit.domain.notification.entity;

public enum NotificationType {

    GROUP_JOIN_REQUEST("그룹 가입 요청이 있습니다."),
    GROUP_JOIN_APPROVED("그룹 가입이 승인되었습니다."),
    GROUP_JOIN_REJECTED("그룹 가입이 거절되었습니다."),
    PUBLISHER_JOIN_REQUEST("출판사의 가입 요청이 있습니다."),
    PUBLISHER_APPROVED("가입이 승인되었습니다."),
    PUBLISHER_REJECTED("가입이 거절되었습니다."),
    DISCUSSION_COMMENT("새로운 댓글이 달렸습니다."),
    COMMENT_REPLY("내 댓글에 답글이 달렸습니다."),
    HIGHLIGHT_COMMENT("내 하이라이트에 댓글이 달렸습니다."),
    HIGHLIGHT_COMMENT_REPLY("내 하이라이트 댓글에 답글이 달렸습니다."),
    GROUP_BANNED("독서모임에서 추방되었습니다.")
    ;

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 