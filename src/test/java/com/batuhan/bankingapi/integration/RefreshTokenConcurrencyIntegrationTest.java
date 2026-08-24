package com.batuhan.bankingapi.integration;

import com.batuhan.bankingapi.dto.RefreshTokenRequest;
import com.batuhan.bankingapi.entity.RefreshToken;
import com.batuhan.bankingapi.entity.Role;
import com.batuhan.bankingapi.entity.User;
import com.batuhan.bankingapi.exception.InvalidRefreshTokenException;
import com.batuhan.bankingapi.repository.UserRepository;
import com.batuhan.bankingapi.service.AuthService;
import com.batuhan.bankingapi.service.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class RefreshTokenConcurrencyIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Test
    void shouldAllowOnlyOneConcurrentRefresh() throws Exception {

        User user = new User();
        user.setFullName("Refresh Concurrency Test");
        user.setEmail(
                "refresh-concurrency-" + UUID.randomUUID() + "@test.com"
        );
        user.setPassword("unused-password");
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);

        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(savedUser);

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(refreshToken.getToken());

        ExecutorService executor =
                Executors.newFixedThreadPool(2);

        CountDownLatch startLatch =
                new CountDownLatch(1);

        try {

            var task = (java.util.concurrent.Callable<Boolean>) () -> {

                startLatch.await();

                try {
                    authService.refresh(request);
                    return true;
                } catch (InvalidRefreshTokenException ex) {
                    return false;
                }
            };

            Future<Boolean> first =
                    executor.submit(task);

            Future<Boolean> second =
                    executor.submit(task);

            startLatch.countDown();

            boolean firstResult =
                    first.get(10, TimeUnit.SECONDS);

            boolean secondResult =
                    second.get(10, TimeUnit.SECONDS);

            long successCount = 0;

            if (firstResult) {
                successCount++;
            }

            if (secondResult) {
                successCount++;
            }

            assertEquals(1, successCount);

        } finally {
            executor.shutdownNow();
        }
    }
}