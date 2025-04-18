package qwerty.chaekit.domain.member.user;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserProfileRepository  extends JpaRepository<UserProfile,Long> {
    Optional<UserProfile> findByMember_Id(Long id);
    boolean existsByNickname(String nickname);

    Optional<UserProfile> findByMember_Username(String username);
}
