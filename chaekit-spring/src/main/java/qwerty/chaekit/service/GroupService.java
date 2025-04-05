package qwerty.chaekit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qwerty.chaekit.domain.group.GroupRepository;
import qwerty.chaekit.domain.group.ReadingGroup;
import qwerty.chaekit.domain.member.user.UserProfile;
import qwerty.chaekit.domain.member.user.UserProfileRepository;
import qwerty.chaekit.dto.group.*;
import qwerty.chaekit.global.exception.ForbiddenException;
import qwerty.chaekit.global.exception.NotFoundException;
import qwerty.chaekit.global.security.resolver.LoginMember;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final UserProfileRepository userProfileRepository;
    private final GroupRepository groupRepository;

    @Transactional
    public GroupPostResponse createGroup(LoginMember loginMember, GroupPostRequest request) {
        UserProfile userProfile = userProfileRepository.findByMember_Id(loginMember.memberId())
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND", "일반 회원이 아니거나 존재하지 않습니다."));

        ReadingGroup groupEntity = ReadingGroup.builder()
                .name(request.name())
                .groupLeader(userProfile)
                .description(request.description())
                .build();
        ReadingGroup savedGroup = groupRepository.save(groupEntity);
        request.tags().forEach(savedGroup::addTag);
        return GroupPostResponse.of(groupEntity);
    }

    @Transactional(readOnly = true)
    public GroupListResponse fetchGroupList(Pageable pageable) {
        Page<GroupFetchResponse> page = groupRepository.findAll(pageable).map(GroupFetchResponse::of);
        return GroupListResponse.builder()
                .groups(page.getContent())
                .currentPage(page.getNumber())
                .totalItems(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Transactional(readOnly = true)
    public GroupFetchResponse fetchGroup(long groupId) {
        ReadingGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("GROUP_NOT_FOUND", "독서모임을 찾을 수 없습니다."));
        return GroupFetchResponse.of(group);
    }

    @Transactional
    public GroupPostResponse updateGroup(LoginMember loginMember, long groupId, GroupPutRequest request) {
        UserProfile userProfile = userProfileRepository.findByMember_Id(loginMember.memberId())
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND", "일반 회원이 아니거나 존재하지 않습니다."));

        ReadingGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("GROUP_NOT_FOUND", "독서모임을 찾을 수 없습니다."));

        if (!group.getGroupLeader().getId().equals(userProfile.getId())) {
            throw new ForbiddenException("GROUP_FORBIDDEN", "독서모임의 리더가 아닙니다.");
        }
        if(request.description() != null) {
            group.updateDescription(request.description());
        }
        return GroupPostResponse.of(group);
    }
}
