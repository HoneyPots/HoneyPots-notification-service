package com.honeypot.domain.notification.repository;

import com.honeypot.domain.notification.entity.NotificationToken;
import com.honeypot.domain.notification.entity.enums.ClientType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

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
        List<NotificationToken> result = notificationTokenRepository.findByMemberId(memberId);

        // Assert
        assertNotNull(result);
        assertEquals(deviceTokens.size(), result.size());
        boolean isAllOwnToken = true;
        boolean isTokenAllValid = true;
        for (NotificationToken token : result) {
            if (!token.getMemberId().equals(memberId)) {
                isAllOwnToken = false;
                break;
            }

            if (!deviceTokens.contains(token.getDeviceToken())) {
                isTokenAllValid = false;
                break;
            }
        }
        assertTrue(isAllOwnToken);
        assertTrue(isTokenAllValid);
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