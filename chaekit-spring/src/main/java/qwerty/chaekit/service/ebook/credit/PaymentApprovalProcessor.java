package qwerty.chaekit.service.ebook.credit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qwerty.chaekit.domain.ebook.credit.payment.CreditPaymentTransaction;
import qwerty.chaekit.domain.ebook.credit.payment.CreditPaymentTransactionRepository;
import qwerty.chaekit.domain.ebook.credit.payment.CreditPaymentTransactionType;
import qwerty.chaekit.domain.ebook.credit.wallet.CreditWallet;
import qwerty.chaekit.domain.ebook.credit.wallet.CreditWalletRepository;
import qwerty.chaekit.dto.external.kakaopay.KakaoPayApproveResponse;
import qwerty.chaekit.global.constant.CreditProduct;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentApprovalProcessor {
    private final CreditPaymentTransactionRepository creditPaymentTransactionRepository;
    private final CreditWalletRepository creditWalletRepository;

    @Transactional
    public void finalizePayment(KakaoPayApproveResponse response, Long userId) {
        CreditWallet wallet = creditWalletRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new IllegalStateException("Credit Wallet not found"));

        int creditAmount = CreditProduct.getCreditProduct(Integer.parseInt(response.item_code())).getCreditAmount();
        if (isFirstPurchase(wallet)) {
            log.info("첫 결제 사용자: userId={}, creditAmount={}", userId, creditAmount);
            creditAmount = (int) (creditAmount * 1.1);
        }

        wallet.addCredit(creditAmount);

        creditPaymentTransactionRepository.save(
                CreditPaymentTransaction.builder()
                        .tid(response.tid())
                        .orderId(response.partner_order_id())
                        .creditProductId(Integer.parseInt(response.item_code()))
                        .creditProductName(response.item_name())
                        .wallet(wallet)
                        .transactionType(CreditPaymentTransactionType.CHARGE)
                        .creditAmount(creditAmount)
                        .paymentAmount(response.amount().total())
                        .approvedAt(response.approved_at())
                        .build()
        );
    }

    private boolean isFirstPurchase(CreditWallet wallet) {
        return creditWalletRepository.existsByUserAndPaymentTransactionsEmpty(wallet.getUser());
    }
}
