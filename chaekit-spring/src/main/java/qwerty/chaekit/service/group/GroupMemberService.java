package qwerty.chaekit.service.group;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qwerty.chaekit.domain.group.ReadingGroup;
import qwerty.chaekit.domain.group.groupmember.GroupMember;
import qwerty.chaekit.domain.group.groupmember.GroupMemberRepository;
import qwerty.chaekit.domain.member.user.UserProfile;
import qwerty.chaekit.dto.group.response.GroupJoinResponse;
import qwerty.chaekit.dto.group.response.GroupMemberResponse;
import qwerty.chaekit.dto.page.PageResponse;
import qwerty.chaekit.global.enums.ErrorCode;
import qwerty.chaekit.global.exception.BadRequestException;
import qwerty.chaekit.global.exception.ForbiddenException;
import qwerty.chaekit.global.security.resolver.UserToken;
import qwerty.chaekit.mapper.GroupMapper;
import qwerty.chaekit.service.notification.NotificationService;
import qwerty.chaekit.service.util.EmailNotificationService;
import qwerty.chaekit.service.util.EntityFinder;
import qwerty.chaekit.service.util.FileService;

@Service
@Transactional
@RequiredArgsConstructor
public class GroupMemberService {
    private final GroupMemberRepository groupMemberRepository;
    private final EmailNotificationService emailNotificationService;
    private final NotificationService notificationService;
    private final FileService fileService;
    private final GroupMapper groupMapper;
    private final EntityFinder entityFinder;

    @Transactional(readOnly = true)
    public PageResponse<GroupMemberResponse> getGroupMembers(long groupId, Pageable pageable) {

        Page<GroupMemberResponse> page = groupMemberRepository.findByReadingGroupId(groupId, pageable)
                .map(
                        groupMember -> GroupMemberResponse.of(
                                groupMember,
                                fileService.convertToPublicImageURL(groupMember.getUser().getProfileImageKey())
                        )
                );
        return PageResponse.of(page);
    }

    @Transactional
    public GroupJoinResponse requestGroupJoin(UserToken userToken, long groupId) {
        UserProfile userProfile = entityFinder.findUser(userToken.userId());
        ReadingGroup group = entityFinder.findGroup(groupId);

        if (group.isMemberAlreadyRequested(userProfile)) {
            throw new ForbiddenException(ErrorCode.ALREADY_JOINED_GROUP);
        }

        GroupMember groupMember = group.addMember(userProfile);

        notificationService.createGroupJoinRequestNotification(
            group.getGroupLeader(),
            userProfile,
            group
        );
        
        return GroupJoinResponse.of(groupMember);
    }

    @Transactional
    public GroupJoinResponse approveJoinRequest(UserToken userToken, long groupId, long userId) {
        GroupApprovalContext ctx = prepareGroupApproval(userToken, groupId, userId);
        UserProfile pendingMember = ctx.pendingMember;
        UserProfile groupLeader = ctx.leader;
        ReadingGroup group = ctx.group;

        GroupMember groupMember = group.approveMember(pendingMember);

        notificationService.createGroupJoinApprovedNotification(
            pendingMember,
            groupLeader,
            group
        );
        
        emailNotificationService.sendReadingGroupApprovalEmail(pendingMember.getMember().getEmail());
        return GroupJoinResponse.of(groupMember);
    }

    @Transactional
    public void rejectJoinRequest(UserToken userToken, long groupId, long userId) {
        GroupApprovalContext ctx = prepareGroupApproval(userToken, groupId, userId);
        UserProfile pendingMember = ctx.pendingMember;
        UserProfile groupLeader = ctx.leader;
        ReadingGroup group = ctx.group;

        group.rejectMember(pendingMember);

        notificationService.createGroupJoinRejectedNotification(
            pendingMember,
            groupLeader,
            group
        );
    }

    private record GroupApprovalContext(
            ReadingGroup group,
            UserProfile leader,
            UserProfile pendingMember
    ) {}

    private GroupApprovalContext prepareGroupApproval(UserToken userToken, long groupId, long userId) {
        UserProfile leaderProfile = entityFinder.findUser(userToken.userId());
        ReadingGroup group = entityFinder.findGroup(groupId);

        if (!group.isLeader(leaderProfile)) {
            throw new ForbiddenException(ErrorCode.GROUP_LEADER_ONLY);
        }

        if (!group.isPendingMember(userId)) {
            throw new ForbiddenException(ErrorCode.GROUP_MEMBER_NOT_PENDING);
        }

        UserProfile memberProfile = entityFinder.findUser(userId);
        return new GroupApprovalContext(group, leaderProfile, memberProfile);
    }

    @Transactional
    public void leaveGroup(UserToken userToken, long groupId) {
        UserProfile userProfile = entityFinder.findUser(userToken.userId());
        ReadingGroup group = entityFinder.findGroup(groupId);

        if (group.getGroupLeader().getId().equals(userProfile.getId())) {
            throw new ForbiddenException(ErrorCode.GROUP_LEADER_CANNOT_LEAVE);
        }

        group.removeMember(userProfile);
    }

    @Transactional(readOnly = true)
    public PageResponse<GroupMemberResponse> fetchPendingList(Pageable pageable, UserToken userToken, long groupId) {
        UserProfile user = entityFinder.findUser(userToken.userId());
        ReadingGroup group = entityFinder.findGroup(groupId);

        if (!group.isLeader(user)) {
            throw new ForbiddenException(ErrorCode.GROUP_LEADER_ONLY);
        }

        Page<GroupMember> pendingMembersPage = groupMemberRepository.findByPendingMemberWithUser(group, pageable);

        Page<GroupMemberResponse> page = pendingMembersPage.map(groupMapper::toGroupMemberResponse);

        return PageResponse.of(page);
    }

    @Transactional
    public void kickGroupMember(UserToken userToken, Long groupId, Long userId) {
        UserProfile loginUser = entityFinder.findUser(userToken.userId());
        UserProfile targetUser = entityFinder.findUser(userId);
        ReadingGroup group = entityFinder.findGroup(groupId);
        if (!group.isLeader(loginUser)) {
            throw new ForbiddenException(ErrorCode.GROUP_LEADER_ONLY);
        }

        if (group.isLeader(targetUser)) {
            throw new BadRequestException(ErrorCode.GROUP_LEADER_CANNOT_LEAVE);
        }
        
        if (group.isNotAcceptedMember(targetUser)) {
            throw new BadRequestException(ErrorCode.GROUP_MEMBER_NOT_JOINED);
        }
        group.removeMember(targetUser);
        notificationService.createGroupBannedNotification(targetUser, group);
    }

    private String getGroupImageURL(ReadingGroup group) {
        return fileService.convertToPublicImageURL(group.getGroupImageKey());
    }
}