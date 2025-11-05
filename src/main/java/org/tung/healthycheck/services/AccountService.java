package org.tung.healthycheck.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tung.healthycheck.dto.AccountProfileDTO;
import org.tung.healthycheck.dto.UpdateProfileDTO;
import org.tung.healthycheck.model.Account;
import org.tung.healthycheck.model.User;
import org.tung.healthycheck.model.UserHealth;
import org.tung.healthycheck.repository.AccountRepository;
import org.tung.healthycheck.repository.UserHealthRepository;
import org.tung.healthycheck.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private UserHealthRepository userHealthRepository;
    @Autowired
    private UserRepository userRepository;

    public Account updateAvatar(UUID accountId, String imageUrl, String newPublicId, String oldPublicId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setImage(imageUrl);
        accountRepository.save(account);

        if (oldPublicId != null && !oldPublicId.isBlank()) {
            boolean deleted = cloudinaryService.deleteImage(oldPublicId);
        }

        return account;
    }

    public AccountProfileDTO getAccount(UUID accountId) {
        Account acc = accountRepository.findById(accountId).orElse(null);
        if (acc == null) return null;
        User user = acc.getUser();
        assert acc.getUser() != null;
        AccountProfileDTO dto = new AccountProfileDTO(
                acc.getId(),
                acc.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.getAddress(),
                acc.getImage(),
                acc.getRole(),
                acc.getUser().getBirth(),
                acc.getUser().getHealthInfo().getHeight(),
                acc.getUser().getHealthInfo().getWeight(),
                acc.getUser().getHealthInfo().getBloodType(),
                acc.getUser().getFullName(),
                acc.getUser().getGender()
        );
        return dto;
    }

    public String updateProfile(UpdateProfileDTO dto) {
        Account account = accountRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy account với accountID: " + dto.getUserId()));
        User user = account.getUser();
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setBirth(dto.getBirth());
        user.setFullName(dto.getFullName());
        user.setGender(dto.getGender());
        userRepository.save(user);

        Optional<UserHealth> existingHealth = userHealthRepository.findByUser(user);
        UserHealth health = existingHealth.orElseGet(UserHealth::new);
        health.setUser(user);
        health.setHeight(dto.getHeight());
        health.setWeight(dto.getWeight());
        health.setBloodType(dto.getBloodType());
        userHealthRepository.save(health);
        return "Cập nhật thông tin thành công";
    }

    public Optional<Account> findByUsername(String username) {
        return accountRepository.findByUsername(username);
    }
}
