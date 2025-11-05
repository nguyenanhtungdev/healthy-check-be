package org.tung.healthycheck.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.tung.healthycheck.model.Account;
import org.tung.healthycheck.model.EmailVerification;
import org.tung.healthycheck.model.Role;
import org.tung.healthycheck.model.User;
import org.tung.healthycheck.repository.AccountRepository;
import org.tung.healthycheck.repository.EmailVerificationRepository;
import org.tung.healthycheck.repository.RoleRepository;
import org.tung.healthycheck.repository.UserRepository;

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

    public Account verifyAndRegister(String email, String password, String code) {
        EmailVerification ev = emailVerificationRepository
                .findByEmailAndCode(email, code)
                .orElseThrow(() -> new RuntimeException("Mã xác thực không hợp lệ"));

        if (ev.isVerified() || ev.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Mã đã hết hạn hoặc đã được sử dụng");
        }

        ev.setVerified(true);
        emailVerificationRepository.save(ev);

        //Không save account ở đây
        Account account = new Account();
        account.setUsername(email);
        account.setPassword(passwordEncoder.encode(password));
        account.setCreatedAt(LocalDateTime.now());
        account.setStatus(true);

        Role userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new RuntimeException("Default role USER not found"));
        account.setRoles(Set.of(userRole));
        account.setRole(userRole.getRoleName());

        //  User và liên kết
        User user = new User();
        user.setEmail(email);
        user.setFullName("");
        user.setGender(null);
        user.setCreatedAt(LocalDateTime.now());
        user.setAccount(account); // liên kết 1-1
        account.setUser(user); // (nếu bạn có mappedBy bên Account)

        // Chỉ cần save user, Hibernate sẽ tự cascade lưu account
        userRepository.save(user);

        return account;
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

    public boolean usernameExists(String username) {
        return accountRepository.findByUsername(username).isPresent();
    }
}
