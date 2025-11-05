package org.tung.healthycheck.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Random;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public String sendVerificationCode(String email) {
        String code = String.format("%06d", new Random().nextInt(999999));

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

            String htmlContent = buildEmailTemplate(code);

            helper.setTo(email);
            helper.setSubject("HealthyCheck - Mã xác thực đăng ký tài khoản");
            helper.setText(htmlContent, true); // true = gửi dạng HTML

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Lỗi khi gửi email xác thực: " + e.getMessage(), e);
        }

        return code;
    }

    private String buildEmailTemplate(String code) {
        return """
        <div style="font-family: 'Segoe UI', sans-serif; max-width: 500px; margin: auto; border: 1px solid #eee; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 10px rgba(0,0,0,0.05);">
            <div style="background: linear-gradient(135deg, #4CAF50, #2E7D32); color: white; text-align: center; padding: 20px;">
                <h2 style="margin: 0;">HealthyCheck</h2>
                <p style="margin: 0;">Xác thực tài khoản của bạn</p>
            </div>
            <div style="padding: 25px; color: #333;">
                <p>Xin chào,</p>
                <p>Cảm ơn bạn đã đăng ký sử dụng ứng dụng <b>HealthyCheck</b>.</p>
                <p>Vui lòng nhập mã xác thực bên dưới để hoàn tất quá trình đăng ký:</p>

                <div style="text-align: center; margin: 30px 0;">
                    <span style="display: inline-block; font-size: 28px; font-weight: bold; letter-spacing: 5px; color: #4CAF50;">
                        %s
                    </span>
                </div>

                <p><b>Thời hạn hiệu lực:</b> 5 phút kể từ khi nhận được email này.</p>
                <p style="margin-top: 30px;">Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email.</p>
            </div>
            <div style="background: #f5f5f5; text-align: center; padding: 15px; font-size: 13px; color: #777;">
                © 2025 HealthyCheck. All rights reserved.
            </div>
        </div>
        """.formatted(code);
    }
}
