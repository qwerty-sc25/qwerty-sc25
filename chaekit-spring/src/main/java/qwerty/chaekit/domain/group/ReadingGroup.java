package qwerty.chaekit.domain.group;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import qwerty.chaekit.domain.BaseEntity;
import qwerty.chaekit.domain.member.user.UserProfile;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReadingGroup extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "group_leader_id", nullable = false)
    private UserProfile groupLeader;

    @OneToMany(mappedBy = "readingGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupTag> tags = new ArrayList<>();

    @Column(nullable = false)
    private String description;

    @OneToMany(mappedBy = "readingGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<GroupMember> groupMembers = new ArrayList<>();

    public void addTag(String tagName) {
        GroupTag groupTag = new GroupTag(this, tagName);
        tags.add(groupTag);
    }

    public void removeTag(String tagName) {
        tags.removeIf(tag -> tag.getTagName().equals(tagName));
    }

    public List<UserProfile> getMembers() {
        return groupMembers.stream().map(GroupMember::getUserProfile).toList();
    }

    public void addMember(UserProfile userProfile) {
        GroupMember groupMember = new GroupMember(this, userProfile);
        groupMembers.add(groupMember);
    }

    public void removeMember(UserProfile userProfile) {
        groupMembers.removeIf(member -> member.getUserProfile().equals(userProfile));
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    @Builder
    public ReadingGroup(String name, UserProfile groupLeader, List<GroupTag> tags, String description) {
        this.name = name;
        this.groupLeader = groupLeader;
        this.tags = (tags != null) ? new ArrayList<>(tags) : new ArrayList<>();
        this.description = description;
    }
}
