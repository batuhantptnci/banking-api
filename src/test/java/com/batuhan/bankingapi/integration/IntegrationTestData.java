package com.batuhan.bankingapi.integration;

import com.batuhan.bankingapi.entity.Role;
import com.batuhan.bankingapi.entity.User;

import java.util.concurrent.atomic.AtomicLong;

final class IntegrationTestData {

    private static final AtomicLong COUNTER =
            new AtomicLong(System.currentTimeMillis());

    private IntegrationTestData() {
    }

    private static long next() {
        return COUNTER.incrementAndGet();
    }

    static String nationalId() {

        long value =
                10_000_000_000L +
                        Math.floorMod(
                                next(),
                                90_000_000_000L
                        );

        return String.valueOf(value);
    }

    static String phone() {

        long value =
                Math.floorMod(
                        next(),
                        1_000_000_000L
                );

        return String.format(
                "5%09d",
                value
        );
    }

    static String customerNumber() {

        long value =
                10_000_000L +
                        Math.floorMod(
                                next(),
                                90_000_000L
                        );

        return String.valueOf(value);
    }

    static User user(
            String fullName,
            String email
    ) {

        User user = new User();

        user.setFullName(fullName);
        user.setEmail(email);
        user.setCustomerNumber(
                customerNumber()
        );
        user.setNationalId(
                nationalId()
        );
        user.setPhone(
                phone()
        );
        user.setPassword(
                "integration-test-password"
        );
        user.setRole(Role.USER);

        return user;
    }

    static String registerBody(
            String fullName,
            String email
    ) {

        return registerBody(
                fullName,
                email,
                nationalId(),
                phone()
        );
    }

    static String registerBody(
            String fullName,
            String email,
            String nationalId,
            String phone
    ) {

        return """
                {
                  "fullName": "%s",
                  "nationalId": "%s",
                  "phone": "%s",
                  "email": "%s",
                  "password": "12345678"
                }
                """.formatted(
                fullName,
                nationalId,
                phone,
                email
        );
    }
}