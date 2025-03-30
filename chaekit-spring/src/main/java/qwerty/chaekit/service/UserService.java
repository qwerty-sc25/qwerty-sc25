package qwerty.chaekit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import qwerty.chaekit.domain.Member.user.UserProfileRepository;
import qwerty.chaekit.dto.UserMemberResponse;
import qwerty.chaekit.global.exception.NotFoundException;
import qwerty.chaekit.global.security.resolver.LoginMember;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserProfileRepository profileRepository;

    public UserMemberResponse getUserProfile(LoginMember loginMember) {
        String nickname = profileRepository.findByMember_Id(loginMember.memberId())
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND", "해당 사용자가 없습니다.")).getNickname();

        return UserMemberResponse.builder()
                .id(loginMember.memberId())
                .nickname(nickname)
                .username(loginMember.username())
                .role(loginMember.role())
                .build();
    }
}
