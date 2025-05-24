package qwerty.chaekit.domain.group.groupmember;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import qwerty.chaekit.domain.BaseEntity;
import qwerty.chaekit.domain.group.ReadingGroup;
import qwerty.chaekit.domain.member.user.UserProfile;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "group_member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupMember extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private ReadingGroup readingGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="user_id", nullable = false)
    private UserProfile user;

    @Column(name = "is_accepted", nullable = false)
    private boolean accepted = false;

    private LocalDateTime approvedAt;

    @Builder
    public GroupMember(ReadingGroup readingGroup, UserProfile user) {
        this.readingGroup = readingGroup;
        this.user = user;
        this.accepted = false;  // 초기에는 미승인 상태
    }

    public ReadingGroup getGroup() {
        return this.readingGroup;
    }

    public UserProfile getMember() {
        return this.user;
    }

    public void approve() {
        this.accepted = true;
        this.approvedAt = LocalDateTime.now();
    }

    public void reject() {
        this.accepted = false;
    }

    public boolean matchesUser(UserProfile user) {
        return matchesUserId(user.getId());
    }
    public boolean matchesUserId(Long userId) {
        return this.user.getId().equals(userId);
    }
    
    public boolean isLeader(){
        return readingGroup.isLeader(user);
    }


}
