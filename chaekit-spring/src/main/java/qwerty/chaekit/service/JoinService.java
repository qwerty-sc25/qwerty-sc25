package qwerty.chaekit.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import qwerty.chaekit.domain.Member.Member;
import qwerty.chaekit.domain.Member.MemberRepository;
import qwerty.chaekit.global.exception.BadRequestException;

@Service
public class JoinService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public JoinService(MemberRepository memberRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {

        this.memberRepository = memberRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void joinProcess(JoinRequest joinRequest) {
        String username = joinRequest.username();
        String password = joinRequest.password();

        Boolean isExist = memberRepository.existsByUsername(username);

        if (isExist) {
            throw new BadRequestException("USER_ALREADY_EXISTS","이미 존재하는 회원입니다");
        }

        Member data=Member.builder()
                .username(username)
                .password(bCryptPasswordEncoder.encode(password))
                .role("ROLE_ADMIN")
                .build();

        memberRepository.save(data);
    }
}
