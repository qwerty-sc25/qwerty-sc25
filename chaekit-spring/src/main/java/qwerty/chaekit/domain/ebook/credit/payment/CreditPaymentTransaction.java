package qwerty.chaekit.domain.ebook.credit.payment;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import qwerty.chaekit.domain.BaseEntity;
import qwerty.chaekit.domain.ebook.credit.wallet.CreditWallet;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "credit_payment_transaction")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreditPaymentTransaction extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String tid;

    @Column(nullable = false, unique = true)
    private String orderId;

    @Column(nullable = false)
    private int creditProductId;

    @Column(nullable = false)
    private String creditProductName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private CreditWallet wallet;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CreditPaymentTransactionType transactionType;

    @Column(nullable = false)
    private int creditAmount;

    @Column(nullable = false)
    private int paymentAmount;

    @Column(nullable = false)
    private LocalDateTime approvedAt;

    @Builder
    public CreditPaymentTransaction(String tid, String orderId, int creditProductId, String creditProductName,
                                    CreditWallet wallet, CreditPaymentTransactionType transactionType, int creditAmount,
                                    int paymentAmount, LocalDateTime approvedAt) {
        this.tid = tid;
        this.orderId = orderId;
        this.creditProductId = creditProductId;
        this.creditProductName = creditProductName;
        this.wallet = wallet;
        this.transactionType = transactionType;
        this.creditAmount = creditAmount;
        this.paymentAmount = paymentAmount;
        this.approvedAt = approvedAt;
    }
}
