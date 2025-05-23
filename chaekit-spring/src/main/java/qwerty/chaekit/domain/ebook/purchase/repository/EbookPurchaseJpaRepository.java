package qwerty.chaekit.domain.ebook.purchase.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import qwerty.chaekit.domain.ebook.Ebook;
import qwerty.chaekit.domain.ebook.purchase.EbookPurchase;
import qwerty.chaekit.domain.member.user.UserProfile;

import java.util.List;
import java.util.Optional;

@Repository
public interface EbookPurchaseJpaRepository extends JpaRepository<EbookPurchase, Long> {
    @Query("SELECT ep FROM EbookPurchase ep JOIN FETCH ep.ebook e WHERE ep.user.id = :userId")
    Page<EbookPurchase> findByUserIdWithEbook(Long userId, Pageable pageable);

    Optional<EbookPurchase> findByUserAndEbook(UserProfile user, Ebook ebook);
    List<EbookPurchase> findByUserIdInAndEbook(List<Long> userIds, Ebook ebook);

    boolean existsByUser_IdAndEbook_Id(Long userId, Long ebookId);
}