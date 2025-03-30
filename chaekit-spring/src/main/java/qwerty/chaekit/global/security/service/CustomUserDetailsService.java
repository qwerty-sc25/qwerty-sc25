package qwerty.chaekit.global.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import qwerty.chaekit.domain.Member.Member;
import qwerty.chaekit.global.security.model.CustomUserDetails;
import qwerty.chaekit.domain.Member.MemberRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member userData = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다."));
        return new CustomUserDetails(
                userData.getId(),
                userData.getUsername(),
                userData.getPassword(),
                userData.getRole().name()
        );
    }
}
