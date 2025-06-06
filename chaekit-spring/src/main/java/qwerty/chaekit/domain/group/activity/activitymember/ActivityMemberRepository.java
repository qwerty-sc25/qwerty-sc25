package qwerty.chaekit.domain.group.activity.activitymember;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import qwerty.chaekit.domain.ebook.Ebook;
import qwerty.chaekit.domain.group.activity.Activity;
import qwerty.chaekit.domain.member.user.UserProfile;

import java.util.List;
import java.util.Optional;


public interface ActivityMemberRepository extends JpaRepository<ActivityMember, Long> {
    boolean existsByUserAndActivity(UserProfile user, Activity activity);
    Page<ActivityMember> findByUserAndActivity_Book(UserProfile user, Ebook ebook, Pageable pageable);
    Page<ActivityMember> findByActivity(Activity activity, Pageable pageable);
    Page<ActivityMember> findByUser(UserProfile user, Pageable pageable);
    Optional<ActivityMember> findByUserAndActivity(UserProfile user, Activity activity);
    List<ActivityMember> findByActivity(Activity activity);
}
