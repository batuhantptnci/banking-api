package com.batuhan.bankingapi.repository;

import com.batuhan.bankingapi.entity.SavedRecipient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedRecipientRepository
        extends JpaRepository<SavedRecipient, Long> {

    List<SavedRecipient>
    findByOwnerIdOrderByCreatedAtDesc(
            Long ownerId
    );

    Optional<SavedRecipient>
    findByIdAndOwnerId(
            Long id,
            Long ownerId
    );

    boolean existsByOwnerIdAndRecipientAccountId(
            Long ownerId,
            Long recipientAccountId
    );
}