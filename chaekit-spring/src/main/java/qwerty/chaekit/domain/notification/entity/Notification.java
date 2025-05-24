package qwerty.chaekit.domain.notification.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import qwerty.chaekit.domain.BaseEntity;
import qwerty.chaekit.domain.group.ReadingGroup;
import qwerty.chaekit.domain.group.activity.discussion.Discussion;
import qwerty.chaekit.domain.group.activity.discussion.comment.DiscussionComment;
import qwerty.chaekit.domain.highlight.entity.Highlight;
import qwerty.chaekit.domain.member.publisher.PublisherProfile;
import qwerty.chaekit.domain.member.user.UserProfile;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private UserProfile receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private UserProfile sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id")
    private PublisherProfile publisher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private ReadingGroup group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "highlight_id")
    private Highlight highlight;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="discussion_id")
    private Discussion discussion;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="discussion_comment_id")
    private DiscussionComment discussionComment;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String message;

    private boolean isRead;

    @Builder
    public Notification(UserProfile receiver, UserProfile sender, PublisherProfile publisher, ReadingGroup group, Highlight highlight, Discussion discussion, DiscussionComment discussionComment, NotificationType type, String message) {
        this.receiver = receiver;
        this.sender = sender;
        this.publisher = publisher;
        this.group = group;
        this.highlight = highlight;
        this.discussion = discussion;
        this.discussionComment = discussionComment;
        this.type = type;
        this.message = message;
        this.isRead = false;
    }

    public void markAsRead() {
        this.isRead = true;
    }
} 