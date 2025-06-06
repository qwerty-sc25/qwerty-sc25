package qwerty.chaekit.service.util;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotificationService {
    private final JavaMailSender javaMailSender;

    // 본인 인증 이메일 전송
    @Async
    public void sendVerificationEmail(String toEmail, String verificationCode) {
        String subject = "책잇 이메일 본인 인증";
        String text = "<h3>본인 인증을 위해 아래 인증코드를 입력하세요.</h3>" +
                "<p>인증 코드: " + verificationCode + "</p>";

        sendEmail(toEmail, subject, text);
    }

    // 출판사 계정 승인 이메일 전송
    @Async
    public void sendPublisherApprovalEmail(String toEmail) {
        String subject = "출판사 계정 승인 알림";
        String text = "<h3>축하합니다! 출판사 계정이 승인되었습니다.</h3>" +
                "<p>이제 출판사 계정을 통해 전자책을 업로드할 수 있습니다.</p>";

        sendEmail(toEmail, subject, text);
    }

    // 독서모임 참여 승인 이메일 전송
    @Async
    public void sendReadingGroupApprovalEmail(String toEmail) {
        String subject = "독서모임 참여 승인 알림";
        String text = "<h3>축하합니다! 독서모임 참여가 승인되었습니다.</h3>" +
                "<p>모임에 참여하여 책을 함께 읽을 수 있습니다.</p>";

        sendEmail(toEmail, subject, text);
    }

    @Async
    public void sendPublisherRejectionEmail(String toEmail, String reason) {
        String subject = "출판사 계정 거절 알림";
        String text = "<h3>안녕하세요, 책잇입니다.</h3>" +
                "<p>출판사 계정이 거절되었습니다. 다시 문의해주세요.</p>" +
                "<p>거절 사유: " + reason + "</p>";

        sendEmail(toEmail, subject, text);
    }

    @Async
    public void sendEbookRejectionEmail(String toEmail, String reason) {
        String subject = "전자책 등록 반려 알림";
        String text = "<h3>안녕하세요, 책잇입니다.</h3>" +
                "<p>전자책 등록이 거절되었습니다. 다시 문의해주세요.</p>" +
                "<p>거절 사유: " + reason + "</p>";

        sendEmail(toEmail, subject, text);
    }

    // 공통된 이메일 전송 메서드
    private void sendEmail(String toEmail, String subject, String text) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(text, true); // true는 HTML 텍스트임을 의미

            javaMailSender.send(message);
        } catch (Exception e) {
            log.warn("Failed to send email", e);
        }
    }
}