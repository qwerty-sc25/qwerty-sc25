package qwerty.chaekit.service.group;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qwerty.chaekit.domain.group.ReadingGroup;
import qwerty.chaekit.domain.group.repository.GroupRepository;
import qwerty.chaekit.domain.member.user.UserProfile;
import qwerty.chaekit.dto.group.request.GroupPatchRequest;
import qwerty.chaekit.dto.group.request.GroupPostRequest;
import qwerty.chaekit.dto.group.response.GroupFetchResponse;
import qwerty.chaekit.dto.group.response.GroupPostResponse;
import qwerty.chaekit.dto.page.PageResponse;
import qwerty.chaekit.global.enums.ErrorCode;
import qwerty.chaekit.global.exception.ForbiddenException;
import qwerty.chaekit.global.exception.NotFoundException;
import qwerty.chaekit.global.security.resolver.UserToken;
import qwerty.chaekit.mapper.GroupMapper;
import qwerty.chaekit.service.util.EntityFinder;
import qwerty.chaekit.service.util.FileService;

@Service
@Transactional
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final FileService fileService;
    private final GroupMapper groupMapper;
    private final EntityFinder entityFinder;

    @Transactional
    public GroupPostResponse createGroup(UserToken userToken, GroupPostRequest request) {
        UserProfile leader = entityFinder.findUser(userToken.userId());

        if(groupRepository.existsReadingGroupByName(request.name())) {
            throw new ForbiddenException(ErrorCode.GROUP_NAME_DUPLICATED);
        }

        String groupImageKey = fileService.uploadGroupImageIfPresent(request.groupImage());

        ReadingGroup groupEntity = ReadingGroup.builder()
                .name(request.name())
                .groupLeader(leader)
                .description(request.description())
                .groupImageKey(groupImageKey)
                .build();
        ReadingGroup savedGroup = groupRepository.save(groupEntity);
        if(request.tags() != null) {
            savedGroup.addTags(request.tags());
        }
        savedGroup.addMember(leader).approve();

        return GroupPostResponse.of(savedGroup, getGroupImageURL(savedGroup));
    }

    @Transactional(readOnly = true)
    public PageResponse<GroupFetchResponse> getAllGroups(UserToken userToken, Pageable pageable) {
        boolean isAnonymous = userToken.isAnonymous();
        Long userId = isAnonymous ? null : userToken.userId();

        Page<GroupFetchResponse> page = groupRepository.findAll(pageable)
                .map(group -> groupMapper.toGroupFetchResponse(group, userId));
        return PageResponse.of(page);
    }

    @Transactional(readOnly = true)
    public PageResponse<GroupFetchResponse> getJoinedGroups(UserToken userToken, Pageable pageable) {
        Long userId = userToken.userId();

        Page<GroupFetchResponse> page = groupRepository.findAllByUserId(userId, pageable)
                .map(group -> groupMapper.toGroupFetchResponse(group, userId));
        return PageResponse.of(page);
    }

    @Transactional(readOnly = true)
    public PageResponse<GroupFetchResponse> getCreatedGroups(UserToken userToken, Pageable pageable) {
        Long userId = userToken.userId();

        Page<GroupFetchResponse> page = groupRepository.findByGroupLeaderId(userId, pageable)
                .map(group -> groupMapper.toGroupFetchResponse(group, userId));

        return PageResponse.of(page);
    }

    @Transactional(readOnly = true)
    public GroupFetchResponse fetchGroup(UserToken userToken, long groupId) {
        boolean isAnonymous = userToken.isAnonymous();
        Long userId = isAnonymous ? null : userToken.userId();

        ReadingGroup group = groupRepository.findByIdWithTags(groupId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.GROUP_NOT_FOUND));
        return groupMapper.toGroupFetchResponse(group, userId);
    }

    @Transactional
    public GroupPostResponse updateGroup(UserToken userToken, long groupId, GroupPatchRequest request) {
        UserProfile user = entityFinder.findUser(userToken.userId());
        ReadingGroup group = entityFinder.findGroup(groupId);

        if (!group.isLeader(user)) {
            throw new ForbiddenException(ErrorCode.GROUP_UPDATE_FORBIDDEN);
        }
        if(request.description() != null) {
            group.updateDescription(request.description());
        }

        String imageKey = fileService.uploadGroupImageIfPresent(request.groupImage());

        if(imageKey != null) {
            group.updateGroupImageKey(imageKey);
        }
        return GroupPostResponse.of(group, getGroupImageURL(group));
    }

    private String getGroupImageURL(ReadingGroup group) {
        return fileService.convertToPublicImageURL(group.getGroupImageKey());
    }
}