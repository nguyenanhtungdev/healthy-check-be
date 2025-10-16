package org.tung.springbootlab3.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.tung.springbootlab3.model.Account;
import org.tung.springbootlab3.model.EmailVerification;
import org.tung.springbootlab3.model.Role;
import org.tung.springbootlab3.model.User;
import org.tung.springbootlab3.repository.AccountRepository;
import org.tung.springbootlab3.repository.EmailVerificationRepository;
import org.tung.springbootlab3.repository.RoleRepository;
import org.tung.springbootlab3.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailVerificationRepository emailVerificationRepository;
    @Autowired
    private EmailService emailService;

    // B1: Gửi mã OTP về email
    public void sendRegisterCode(String email) {
        String code = emailService.sendVerificationCode(email);

        EmailVerification ev = new EmailVerification();
        ev.setEmail(email);
        ev.setCode(code);
        ev.setCreatedAt(LocalDateTime.now());
        ev.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        ev.setVerified(false);
        emailVerificationRepository.save(ev);
    }

    // B2: Xác thực mã và tạo tài khoản
    public Account verifyAndRegister(String email, String password, String code) {
        EmailVerification ev = emailVerificationRepository
                .findByEmailAndCode(email, code)
                .orElseThrow(() -> new RuntimeException("Mã xác thực không hợp lệ"));

        if (ev.isVerified() || ev.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Mã đã hết hạn hoặc đã được sử dụng");
        }

        ev.setVerified(true);
        emailVerificationRepository.save(ev);

        Account account = new Account();
        account.setUsername(email);
        account.setPassword(passwordEncoder.encode(password));
        account.setCreatedAt(LocalDateTime.now());
        account.setStatus(true);

        Role userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new RuntimeException("Default role USER not found"));
        account.setRoles(Set.of(userRole));
        account.setRole(userRole.getRoleName());
        return accountRepository.save(account);
    }

    //Đăng ký bình thường
    public Account register(String username, String password) {
        if (accountRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        Account account = new Account();
        account.setUsername(username);
        account.setPassword(passwordEncoder.encode(password));
        account.setCreatedAt(LocalDateTime.now());
        account.setStatus(true);

        Role userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new RuntimeException("Default role USER not found"));
        account.setRoles(Set.of(userRole));
        account.setRole(userRole.getRoleName());

        return accountRepository.save(account);
    }

    // Đổi mật khẩu bằng email OTP
    public void resetPassword(String email, String newPassword, String code) {
        EmailVerification ev = emailVerificationRepository
                .findByEmailAndCode(email, code)
                .orElseThrow(() -> new RuntimeException("Mã xác thực không hợp lệ"));

        Account acc = accountRepository.findByUsername(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));

        acc.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(acc);
    }

    public Account validate(String username, String password) {
        return accountRepository.findByUsername(username)
                .filter(acc -> passwordEncoder.matches(password, acc.getPassword()))
                .orElse(null);
    }
}
