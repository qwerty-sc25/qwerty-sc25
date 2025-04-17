package qwerty.chaekit.global.security.model;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

// 1. JWT를 읽을 때 사용
// 2. AuthenticationManager에서 로그인할 때 사용
public class CustomUserDetails implements UserDetails {
    @Getter
    private final Long memberId;
    @Getter
    private final Long profileId;
    private final String email;
    private final String password;
    private final String role;

    public CustomUserDetails(Long memberId, Long profileId, String username, String role) {
        this.memberId = memberId;
        this.profileId = profileId;
        this.email = username;
        this.password = null;
        this.role = role;
    }

    public CustomUserDetails(Long memberId, Long profileId, String username, String password, String role) {
        this.memberId = memberId;
        this.profileId = profileId;
        this.email = username;
        this.password = password;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add((GrantedAuthority) () -> role);
        return collection;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
