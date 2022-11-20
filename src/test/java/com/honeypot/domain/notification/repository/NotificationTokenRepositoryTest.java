package com.honeypot.domain.notification.repository;

import com.honeypot.domain.notification.entity.NotificationToken;
import com.honeypot.domain.notification.entity.enums.ClientType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.Optional;

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
    @DisplayName("MemberId와 DeviceToken을 이용한 NotificationToken 조회 성공")
    void findByMemberAndDeviceToken() {
        // Arrange
        String deviceToken = "notificationDeviceToken";

        Long memberId = 123L;
        createNotificationToken(memberId, deviceToken, ClientType.WEB);

        // Act
        Optional<NotificationToken> result
                = notificationTokenRepository.findByMemberIdAndDeviceToken(memberId, deviceToken);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(memberId, result.get().getMemberId());
        assertEquals(deviceToken, result.get().getDeviceToken());
    }

    @Test
    @DisplayName("MemberId와 DeviceToken을 이용한 NotificationToken 조회 실패")
    void findByMemberAndDeviceToken_NotFound() {
        // Arrange
        String deviceToken = "notificationDeviceToken";

        Long memberId = 123L;

        // Act
        Optional<NotificationToken> result
                = notificationTokenRepository.findByMemberIdAndDeviceToken(memberId, deviceToken);

        // Assert
        assertFalse(result.isPresent());
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