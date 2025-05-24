package qwerty.chaekit.global.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // BAD_REQUEST
    // book
    EBOOK_FILE_MISSING("EBOOK_FILE_MISSING", "파일이 누락되었습니다"),
    EBOOK_FILE_SIZE_EXCEEDED("EBOOK_FILE_SIZE_EXCEEDED", "파일 크기가 초과되었습니다"),
    INVALID_EBOOK_FILE("INVALID_EBOOK_FILE", "유효하지 않은 전자책 파일입니다"),
    // highlight
    BOOK_ID_REQUIRED("BOOK_ID_REQUIRED", "책 ID가 필요합니다"),
    // member
    MEMBER_ALREADY_EXISTS("MEMBER_ALREADY_EXISTS", "이미 존재하는 회원입니다"),
    PUBLISHER_ALREADY_EXISTS("PUBLISHER_ALREADY_EXISTS", "이미 존재하는 출판사 이름입니다"),
    NICKNAME_ALREADY_EXISTS("NICKNAME_ALREADY_EXISTS", "이미 존재하는 닉네임입니다"),
    // group
    GROUP_NAME_DUPLICATED("GROUP_NAME_DUPLICATED", "독서모임 이름이 중복되었습니다"),
    ACTIVITY_TIME_CONFLICT("ACTIVITY_TIME_CONFLICT", "이미 등록된 독서모임 일정과 겹칩니다"),
    ACTIVITY_TIME_INVALID("ACTIVITY_TIME_INVALID", "시작일과 종료일이 올바르지 않습니다"),
    GROUP_MEMBER_NOT_PENDING("GROUP_MEMBER_NOT_PENDING", "가입 대기 중인 회원이 아닙니다"),
    GROUP_MEMBER_NOT_JOINED("GROUP_MEMBER_NOT_JOINED", "승인된 모임원이 아닙니다"),
    // activity
    ACTIVITY_ALREADY_JOINED("ACTIVITY_ALREADY_JOINED", "이미 가입된 독서모임 활동입니다"),
    ACTIVITY_NOT_JOINED("ACTIVITY_NOT_JOINED", "가입되지 않은 독서모임 활동입니다"),
    ACTIVITY_ALREADY_ENDED("ACTIVITY_ALREADY_ENDED", "이미 종료된 독서모임 활동입니다"),
    ACTIVITY_BOOK_NOT_OWNED("ACTIVITY_BOOK_NOT_OWNED", "독서모임 활동에 등록된 전자책이 존재하지 않습니다"),
    // discussion
    DISCUSSION_NOT_YOURS("DISCUSSION_NOT_YOURS", "해당 토론은 본인이 생성한 것이 아닙니다"),
    // discussion_comment
    REPLY_CANNOT_HAVE_CHILD("REPLY_CANNOT_HAVE_CHILD", "답글에는 다시 답글을 달 수 없습니다."),
    DISCUSSION_COMMENT_DELETED("DISCUSSION_COMMENT_DELETED", "이미 삭제된 댓글입니다."),
    // email
    EMAIL_VERIFICATION_FAILED("EMAIL_VERIFICATION_FAILED", "이메일 인증에 실패했습니다."),
    EMAIL_SEND_FAILED("EMAIL_SEND_FAILED", "이메일 전송에 실패했습니다."),
    // file
    FILE_MISSING("FILE_MISSING", "파일이 누락되었습니다"),
    INVALID_EXTENSION("INVALID_EXTENSION", "허용되지 않는 확장자입니다"),
    FILE_SIZE_EXCEEDED("FILE_SIZE_EXCEED", "파일 크기가 초과되었습니다"),
    // admin
    PUBLISHER_ALREADY_ACCEPTED("PUBLISHER_ALREADY_ACCEPTED", "이미 승인된 출판사입니다"),
    // credit payment
    INVALID_CREDIT_PRODUCT_ID("INVALID_CREDIT_PRODUCT_ID", "유효하지 않은 크레딧 상품 ID입니다"),
    INVALID_PAYMENT_SESSION("INVALID_PAYMENT_SESSION", "결제 세션이 만료됐거나 유효하지 않습니다"),
    // credit usage
    CREDIT_NOT_ENOUGH("CREDIT_NOT_ENOUGH", "크레딧이 부족합니다"),
    // credit wallet
    CREDIT_WALLET_NOT_FOUND("CREDIT_WALLET_NOT_FOUND", "크레딧 지갑이 존재하지 않습니다"),
    // ebook purchase
    EBOOK_ALREADY_PURCHASED("EBOOK_ALREADY_PURCHASED", "이미 구매한 전자책입니다"),

    // UNAUTHORIZED
    INVALID_ACCESS_TOKEN("INVALID_ACCESS_TOKEN", "유효하지 않은 Access Token입니다"),
    INVALID_REFRESH_TOKEN("INVALID_REFRESH_TOKEN", "유효하지 않은 Refresh Token입니다"),
    EXPIRED_ACCESS_TOKEN("EXPIRED_ACCESS_TOKEN", "Access Token이 만료되었습니다"),
    EXPIRED_REFRESH_TOKEN("EXPIRED_REFRESH_TOKEN", "Refresh Token이 만료되었습니다"),

    // FORBIDDEN
    GROUP_MEMBER_ONLY("GROUP_MEMBER_ONLY", "독서모임에 가입된 회원만 사용할 수 있는 기능입니다"),
    GROUP_UPDATE_FORBIDDEN("GROUP_UPDATE_FORBIDDEN", "독서모임 수정 권한이 없습니다"),
    HIGHLIGHT_NOT_YOURS("HIGHLIGHT_NOT_YOURS", "하이라이트는 본인만 수정할 수 있습니다"),
    HIGHLIGHT_NOT_SEE("HIGHLIGHT_NOT_SEE","다른사람이 작성한 비공개 하이라이트는 볼 수 없습니다."),
    GROUP_LEADER_ONLY("GROUP_LEADER_ONLY", "모임지기만 사용할 수 있는 기능입니다"),
    ACTIVITY_GROUP_MISMATCH("ACTIVITY_GROUP_MISMATCH", "해당 독서모임의 활동이 아닙니다"),
    ACTIVITY_MEMBER_ONLY("ACTIVITY_MEMBER_ONLY", "해당 독서모임 활동에 가입한 경우에만 가능합니다"),
    DISCUSSION_COMMENT_NOT_YOURS("DISCUSSION_COMMENT_NOT_YOURS", "해당 댓글은 본인이 작성한 것이 아닙니다"),
    EBOOK_NOT_PURCHASED("EBOOK_NOT_PURCHASED", "해당 전자책을 구매한 회원만 사용할 수 있는 기능입니다"),
    PUBLISHER_NOT_APPROVED("PUBLISHER_NOT_APPROVED", "현재 출판사 계정이 승인되지 않았습니다"),
    DISCUSSION_HAS_COMMENTS("DISCUSSION_HAS_COMMENTS", "댓글이 달린 토론은 삭제할 수 없습니다"),

    LOGIN_REQUIRED("LOGIN_REQUIRED", "로그인이 필요합니다"),
    ONLY_USER("ONLY_USER", "일반 회원만 사용할 수 있는 기능입니다"),
    ONLY_PUBLISHER("ONLY_PUBLISHER", "출판사 회원만 사용할 수 있는 기능입니다"),
    ONLY_ADMIN("ONLY_ADMIN", "관리자만 사용할 수 있는 기능입니다"),


    // NOT_FOUND
    MEMBER_NOT_FOUND("MEMBER_NOT_FOUND", "해당 회원이 존재하지 않습니다"),
    EBOOK_NOT_FOUND("EBOOK_NOT_FOUND", "해당 전자책이 존재하지 않습니다"),
    PUBLISHER_NOT_FOUND("PUBLISHER_NOT_FOUND", "해당 출판사가 존재하지 않습니다"),
    USER_NOT_FOUND("USER_NOT_FOUND", "일반 회원이 아니거나 존재하지 않습니다"),
    GROUP_NOT_FOUND("GROUP_NOT_FOUND", "해당 독서모임이 존재하지 않습니다"),
    HIGHLIGHT_NOT_FOUND("HIGHLIGHT_NOT_FOUND", "해당 하이라이트가 존재하지 않습니다"),
    ACTIVITY_NOT_FOUND("ACTIVITY_NOT_FOUND", "해당 활동이 존재하지 않습니다"),
    DISCUSSION_NOT_FOUND("DISCUSSION_NOT_FOUND", "해당 토론이 존재하지 않습니다"),
    DISCUSSION_COMMENT_NOT_FOUND("DISCUSSION_COMMENT_NOT_FOUND", "해당 댓글이 존재하지 않습니다"),


    // Comment related
    COMMENT_NOT_FOUND("COMMENT_NOT_FOUND", "해당 댓글이 존재하지 않습니다"),
    COMMENT_NOT_YOURS("COMMENT_NOT_YOURS", "댓글은 본인만 수정/삭제할 수 있습니다"),
    COMMENT_PARENT_MISMATCH("COMMENT_PARENT_MISMATCH", "답글달 댓글이 없습니다"),
    HIGHLIGHT_NOT_PUBLIC("HIGHLIGHT_NOT_PUBLIC", "공개되지 않은 하이라이트에는 댓글을 작성할 수 없습니다"),
    COMMENT_ID_REQUIRED("COMMENT_ID_REQUIRED", "이모티콘 반응을 추가하려면 댓글이 필요합니다"),
    
    // Reaction related
    REACTION_NOT_FOUND("REACTION_NOT_FOUND", "해당 이모티콘이 존재하지 않습니다"),
    NOT_REACTION_AUTHOR("NOT_REACTION_AUTHOR", "본인만 삭제할 수 있습니다"),
    REACTION_ALREADY_EXISTS("REACTION_ALREADY_EXISTS", "이미 동일한 이모티콘 반응을 추가했습니다"),

    // Exception Handler
    INVALID_INPUT("INVALID_INPUT", "입력값이 유효하지 않습니다"),
    INVALID_HTTP_MESSAGE("INVALID_HTTP_MESSAGE", "HTTP 요청 본문이 잘못된 형식입니다."),

    METHOD_NOT_ALLOWED("METHOD_NOT_ALLOWED", "지원하지 않는 HTTP 메서드입니다"),
    NO_RESOURCE_FOUND("NO_RESOURCE_FOUND", "존재하지 않는 경로입니다"),

    ALREADY_JOINED_GROUP("ALREADY_JOINED_GROUP", "이미 가입한 그룹입니다"),
    GROUP_LEADER_CANNOT_LEAVE("GROUP_LEADER_CANNOT_LEAVE", "그룹장은 그룹을 탈퇴할 수 없습니다"),
    ACTIVITY_ID_REQUIRED("ACTIVITY_ID_REQUIRED", "활동 ID가 필요합니다"),
    HIGHLIGHT_ALREADY_SHARED("HIGHLIGHT_ALREADY_SHARED", "활동에 가입되면 하이라이트는 수정할 수 없습니다"),
    HIGHLIGHT_ALREADY_PUBLIC("HIGHLIGHT_ALREADY_PUBLIC", "공개된 하이라이트는 수정할 수 없습니다"),

    // Notification related
    NOTIFICATION_NOT_FOUND("NOTIFICATION_NOT_FOUND", "해당 알림이 존재하지 않습니다"),
    NOTIFICATION_NOT_YOURS("NOTIFICATION_NOT_YOURS", "본인의 알림만 읽음 처리할 수 있습니다"),
    NOTIFICATION_CONTENT_DELETED("NOTIFICATION_CONTENT_DELETED", "알림과 관련된 내용이 삭제되었습니다"),
    ;

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
