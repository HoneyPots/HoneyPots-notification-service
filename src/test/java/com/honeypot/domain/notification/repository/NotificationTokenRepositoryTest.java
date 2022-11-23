package com.honeypot.domain.notification.repository;

import com.honeypot.domain.notification.entity.NotificationToken;
import com.honeypot.domain.notification.entity.enums.ClientType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class NotificationTokenRepositoryTest {

    @Autowired
    private NotificationTokenRepository notificationTokenRepository;

    @BeforeEach
    public void before() {
        notificationTokenRepository.deleteAll();
    }

    @Test
    @DisplayName("MemberId를 이용한 NotificationToken 조회 성공")
    void findByMemberId() {
        // Arrange
        Long memberId = 123L;

        Random random = new Random();
        List<String> deviceTokens = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            String generatedToken = random.ints('a', 'z' + 1)
                    .limit(10)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
            deviceTokens.add(generatedToken);
            createNotificationToken(memberId, generatedToken, ClientType.WEB);
        }

        // Act
        Flux<NotificationToken> result = notificationTokenRepository.findByMemberId(memberId);

        // Assert
        StepVerifier.create(result)
                .expectNextCount(deviceTokens.size())
                .assertNext(token -> {
                    if (!token.getMemberId().equals(memberId)) {
                        fail();
                    }

                    if (!deviceTokens.contains(token.getDeviceToken())) {
                        fail();
                    }
                });
    }

    @Test
    @DisplayName("MemberId와 DeviceToken을 이용한 NotificationToken 조회 성공")
    void findByMemberAndDeviceToken() {
        // Arrange
        String deviceToken = "notificationDeviceToken";

        Long memberId = 123L;
        createNotificationToken(memberId, deviceToken, ClientType.AOS);

        // Act
        Mono<NotificationToken> result
                = notificationTokenRepository.findByMemberIdAndDeviceToken(memberId, deviceToken);

        // Assert
        StepVerifier.create(result)
                .assertNext(token -> {
                    assertEquals(memberId, token.getMemberId());
                    assertEquals(deviceToken, token.getDeviceToken());
                })
                .expectNextCount(1);
    }

    @Test
    @DisplayName("MemberId와 DeviceToken을 이용한 NotificationToken 조회 실패")
    void findByMemberAndDeviceToken_NotFound() {
        // Arrange
        String deviceToken = "notificationDeviceToken";

        Long memberId = 123L;

        // Act
        Mono<NotificationToken> result
                = notificationTokenRepository.findByMemberIdAndDeviceToken(memberId, deviceToken);

        // Assert
        StepVerifier.create(result).expectNextCount(0).verifyComplete();
    }

    private void createNotificationToken(Long memberId, String token, ClientType clientType) {
        NotificationToken created = NotificationToken.builder()
                .memberId(memberId)
                .deviceToken(token)
                .clientType(clientType)
                .build();
        notificationTokenRepository.save(created);
    }

}