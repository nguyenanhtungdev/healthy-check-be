package org.tung.springbootlab3.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tung.springbootlab3.model.Account;
import org.tung.springbootlab3.repository.AccountRepository;

import java.util.UUID;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

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

    public Account getAccount(UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }
}
