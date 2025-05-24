package qwerty.chaekit.domain.group;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.springframework.lang.Nullable;
import qwerty.chaekit.domain.BaseEntity;
import qwerty.chaekit.domain.group.groupmember.GroupMember;
import qwerty.chaekit.domain.group.grouptag.GroupTag;
import qwerty.chaekit.domain.member.user.UserProfile;
import qwerty.chaekit.dto.group.enums.MyMemberShipStatus;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "reading_group")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@BatchSize(size = 20)
public class ReadingGroup extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_leader_id", nullable = false)
    private UserProfile groupLeader;

    @OneToMany(mappedBy = "readingGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 20)
    private List<GroupTag> tags = new ArrayList<>();
    
    @Column(length = 5000)
    private String description;

    @OneToMany(mappedBy = "readingGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 20)
    private final List<GroupMember> groupMembers = new ArrayList<>();

    private String groupImageKey;

    public void addTags(List<String> tagNames) {
        for (String tagName : tagNames) {
            addTag(tagName);
        }
    }

    public void addTag(String tagName) {
        GroupTag groupTag = new GroupTag(this, tagName);
        tags.add(groupTag);
    }

    public void removeTag(String tagName) {
        tags.removeIf(tag -> tag.getTagName().equals(tagName));
    }

    public GroupMember addMember(UserProfile user) {
        GroupMember groupMember = new GroupMember(this, user);
        groupMembers.add(groupMember);
        return groupMember;
    }

    public GroupMember approveMember(UserProfile user){
        return groupMembers.stream()
                .filter(groupMember -> groupMember.getMember().getId().equals(user.getId()))
                .findFirst()
                .map(groupMember -> {groupMember.approve(); return groupMember;})
                .orElse(null);
    }

    public void removeMember(UserProfile user) {
        groupMembers.removeIf(member -> member.getMember().getId().equals(user.getId()));
    }

    public void rejectMember(UserProfile user) {
        groupMembers.stream()
                .filter(member -> member.getMember().getId().equals(user.getId()))
                .findFirst()
                .ifPresent(GroupMember::reject);
        removeMember(user);
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateGroupImageKey(String groupImageKey) {
        this.groupImageKey = groupImageKey;
    }

    public MyMemberShipStatus getMemberShipStatus(@Nullable Long userId) {
        if(userId == null) {
            return MyMemberShipStatus.NONE;
        }
        if (isLeader(userId)) {
            return MyMemberShipStatus.OWNED;
        }
        return groupMembers.stream()
                .filter(member -> member.getMember().getId().equals(userId))
                .findFirst()
                .map(member -> member.isAccepted() ? MyMemberShipStatus.JOINED : MyMemberShipStatus.PENDING)
                .orElse(MyMemberShipStatus.NONE);
    }

    public long memberCount() {
        return groupMembers.stream()
                .filter(GroupMember::isAccepted)
                .count();
    }
    
    public boolean isLeader(UserProfile user) {
        return isLeader(user.getId());
    }

    public boolean isLeader(Long userId) {
        return groupLeader.getId().equals(userId);
    }

    public boolean isNotAcceptedMember(UserProfile user) {
        return groupMembers.stream()
                .noneMatch(member -> member.matchesUser(user) && member.isAccepted());
    }

    public boolean isMemberAlreadyRequested(UserProfile user) {
        return groupMembers.stream()
                .anyMatch(gm -> gm.matchesUser(user));
    }

    public boolean isPendingMember(Long userId) {
        return groupMembers.stream()
                .anyMatch(gm -> gm.matchesUserId(userId) && !gm.isAccepted());
    }

    @Builder
    public ReadingGroup(Long id, String name, UserProfile groupLeader, List<GroupTag> tags, String description, String groupImageKey) {
        this.id = id;
        this.name = name;
        this.groupLeader = groupLeader;
        this.tags = (tags != null) ? new ArrayList<>(tags) : new ArrayList<>();
        this.description = description;
        this.groupImageKey = groupImageKey;
    }
}
