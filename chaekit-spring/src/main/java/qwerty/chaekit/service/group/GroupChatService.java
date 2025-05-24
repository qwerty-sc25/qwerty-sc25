package qwerty.chaekit.service.group;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qwerty.chaekit.domain.group.ReadingGroup;
import qwerty.chaekit.domain.group.chat.GroupChat;
import qwerty.chaekit.domain.group.chat.repository.GroupChatRepository;
import qwerty.chaekit.domain.member.user.UserProfile;
import qwerty.chaekit.dto.group.chat.GroupChatRequest;
import qwerty.chaekit.dto.group.chat.GroupChatResponse;
import qwerty.chaekit.dto.page.PageResponse;
import qwerty.chaekit.global.enums.ErrorCode;
import qwerty.chaekit.global.exception.ForbiddenException;
import qwerty.chaekit.global.security.resolver.UserToken;
import qwerty.chaekit.service.util.EntityFinder;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupChatService {
    private final GroupChatRepository groupChatRepository;
    private final EntityFinder entityFinder;

    @Transactional
    public GroupChatResponse createChat(UserToken userToken, Long groupId, GroupChatRequest request) {
        UserProfile user = entityFinder.findUser(userToken.userId());
        ReadingGroup group = entityFinder.findGroup(groupId);

        if (group.isNotAcceptedMember(user)) {
            throw new ForbiddenException(ErrorCode.MEMBER_NOT_FOUND);
        }

        GroupChat chat = GroupChat.builder()
                .group(group)
                .author(user)
                .content(request.content())
                .build();

        return GroupChatResponse.of(groupChatRepository.save(chat));
    }

    public PageResponse<GroupChatResponse> getChats(Long groupId, Pageable pageable) {
        ReadingGroup group = entityFinder.findGroup(groupId);
        Page<GroupChat> chats = groupChatRepository.findByGroupOrderByCreatedAtDesc(group, pageable);

        return PageResponse.of(chats.map(GroupChatResponse::of));
    }
}