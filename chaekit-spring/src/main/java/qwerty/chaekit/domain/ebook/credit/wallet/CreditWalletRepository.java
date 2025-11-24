package qwerty.chaekit.domain.ebook.credit.wallet;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import qwerty.chaekit.domain.member.user.UserProfile;

import java.util.Optional;

public interface CreditWalletRepository extends JpaRepository<CreditWallet, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from CreditWallet w where w.user.id = :userId")
    Optional<CreditWallet> findByUserIdForUpdate(@Param("userId") Long userId);

    Optional<CreditWallet> findByUser_Id(Long userId);
    boolean existsByUserAndPaymentTransactionsEmpty(UserProfile user);
}
