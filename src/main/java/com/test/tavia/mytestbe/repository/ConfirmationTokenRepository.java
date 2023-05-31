package com.test.tavia.mytestbe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.tavia.mytestbe.entity.ConfirmationToken;

public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {
    ConfirmationToken findByConfirmationToken(String confirmationToken);
}
