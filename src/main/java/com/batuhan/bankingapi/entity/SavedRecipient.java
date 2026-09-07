package com.batuhan.bankingapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "saved_recipients",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_saved_recipients_owner_account",
                        columnNames = {
                                "owner_user_id",
                                "recipient_account_id"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class SavedRecipient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "owner_user_id",
            nullable = false
    )
    private User owner;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "recipient_account_id",
            nullable = false
    )
    private Account recipientAccount;

    @Column(
            nullable = false,
            length = 40
    )
    private String nickname;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt =
            LocalDateTime.now();
}