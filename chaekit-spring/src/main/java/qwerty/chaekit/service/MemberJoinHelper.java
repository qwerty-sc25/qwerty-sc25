package qwerty.chaekit.service;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import qwerty.chaekit.domain.Member.Member;
import qwerty.chaekit.domain.Member.enums.Role;
import qwerty.chaekit.domain.Member.MemberRepository;
import qwerty.chaekit.global.exception.BadRequestException;

@Service
@RequiredArgsConstructor
public class MemberJoinHelper {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @NotNull
    public Member saveMember(String username, String password, Role role) {
        validateUsername(username);
        return memberRepository.save(Member.builder()
                .username(username)
                .password(bCryptPasswordEncoder.encode(password))
                .role(role)
                .build());
    }

    private void validateUsername(String username) {
        if (memberRepository.existsByUsername(username)) {
            throw new BadRequestException("MEMBER_ALREADY_EXISTS","이미 존재하는 회원입니다");
        }
    }
}
