package com.honeypot.domain.notification.repository;

import com.honeypot.domain.notification.entity.NotificationHistory;
import com.honeypot.domain.notification.entity.enums.NotificationType;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@DataMongoTest
class NotificationHistoryRepositoryTest {

    @Autowired
    private NotificationHistoryRepository notificationHistoryRepository;

    @BeforeEach
    public void before() {
        notificationHistoryRepository.deleteAll().subscribe();
    }

    @AfterEach
    public void after() {
        notificationHistoryRepository.deleteAll().subscribe();
    }

    @Test
    @DisplayName("회원별 NotificationHistory 목록 조회 성공 (총 개수 > 조회 요청 개수)")
    void findByMemberId_RequestSizeSmallerThanExistSize() {
        // Arrange
        Long memberId = 1L;
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("createdAt")));

        createNotificationHistory(memberId, 5)
                .doOnComplete(
                        () -> {
                            // Act & Assert
                            StepVerifier.create(notificationHistoryRepository.findByMemberId(memberId, pageable))
                                    .expectNextCount(10)
                                    .verifyComplete();
                        }
                );
    }

    @Test
    @DisplayName("회원별 NotificationHistory 목록 조회 성공 (총 개수 < 조회 요청 개수)")
    void findByMemberId_RequestSizeGreaterThanExistSize() {
        // Arrange
        Long memberId = 1L;
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("createdAt")));

        createNotificationHistory(memberId, 5)
                .doOnComplete(
                        () -> {
                            // Act & Assert
                            StepVerifier.create(notificationHistoryRepository.findByMemberId(memberId, pageable))
                                    .expectNextCount(5)
                                    .verifyComplete();
                        }
                );
    }

    private Flux<NotificationHistory> createNotificationHistory(Long memberId, int count) {
        List<NotificationHistory> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            LocalDateTime now = LocalDateTime.now();
            NotificationHistory created = NotificationHistory.builder()
                    .id(RandomString.make())
                    .memberId(memberId)
                    .title("title")
                    .content("content")
                    .type(NotificationType.LIKE_REACTION_TO_POST)
                    .createdAt(now)
                    .lastModifiedAt(now)
                    .build();
        }
        return notificationHistoryRepository.saveAll(list);
    }

}