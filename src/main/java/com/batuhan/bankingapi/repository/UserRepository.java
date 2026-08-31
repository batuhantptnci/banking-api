package com.batuhan.bankingapi.repository;

import com.batuhan.bankingapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByNationalId(String nationalId);

    boolean existsByPhone(String phone);

    boolean existsByCustomerNumber(String customerNumber);

    Optional<User> findByEmail(String email);

    Optional<User> findByCustomerNumberOrNationalId(
            String customerNumber,
            String nationalId
    );
}