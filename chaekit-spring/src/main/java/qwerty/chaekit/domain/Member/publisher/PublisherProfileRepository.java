package qwerty.chaekit.domain.Member.publisher;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PublisherProfileRepository extends JpaRepository<PublisherProfile,Long> {
    Optional<PublisherProfile> findByMember_Username(String username);
    boolean existsByPublisherName(String publisherName);
}
