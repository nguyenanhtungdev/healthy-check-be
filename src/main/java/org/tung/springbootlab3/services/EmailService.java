package org.tung.springbootlab3.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.Random;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public String sendVerificationCode(String email) {
        String code = String.format("%06d", new Random().nextInt(999999));
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("HealthyCheck - Mã xác thực đăng ký tài khoản");
        message.setText("Mã xác thực của bạn là: " + code + "\nThời hạn: 5 phút.");

        mailSender.send(message);
        return code;
    }
}
