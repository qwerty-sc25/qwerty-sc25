package qwerty.chaekit.domain.group.activity.activitymember;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import qwerty.chaekit.domain.BaseEntity;
import qwerty.chaekit.domain.group.activity.Activity;
import qwerty.chaekit.domain.member.user.UserProfile;

@Entity
@Getter
@Table(name = "activity_member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ActivityMember extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id")
    private Activity activity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserProfile user;

    @Builder
    public ActivityMember(Activity activity, UserProfile user) {
        this.activity = activity;
        this.user = user;
    }
}
