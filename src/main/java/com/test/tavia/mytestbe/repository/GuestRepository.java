package com.test.tavia.mytestbe.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.tavia.mytestbe.entity.Guest;

public interface GuestRepository extends JpaRepository<Guest, String> {

    boolean existsByEmail(String email);

    Optional<Guest> findByEmail(String email);

}
