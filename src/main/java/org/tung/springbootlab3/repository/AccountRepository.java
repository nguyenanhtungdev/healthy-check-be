package org.tung.springbootlab3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tung.springbootlab3.model.Account;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByUsername(String username);
}
