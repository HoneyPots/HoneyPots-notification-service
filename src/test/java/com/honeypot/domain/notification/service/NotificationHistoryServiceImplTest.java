package com.honeypot.domain.notification.service;

import com.honeypot.common.errors.exceptions.NotFoundException;
import com.honeypot.domain.notification.dto.CommentNotificationResource;
import com.honeypot.domain.notification.dto.NotificationHistoryDto;
import com.honeypot.domain.notification.dto.PostNotificationResource;
import com.honeypot.domain.notification.entity.NotificationHistory;
import com.honeypot.domain.notification.entity.enums.NotificationType;
import com.honeypot.domain.notification.entity.enums.PostType;
import com.honeypot.domain.notification.mapper.NotificationHistoryMapper;
import com.honeypot.domain.notification.repository.NotificationHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
class NotificationHistoryServiceImplTest {

    private final NotificationHistoryMapper notificationHistoryMapper = Mappers.getMapper(NotificationHistoryMapper.class);

    @Mock
    private NotificationHistoryRepository notificationHistoryRepository;

    @Mock
    private NotificationHistoryMapper notificationHistoryMapperMock;

    @InjectMocks
    private NotificationHistoryServiceImpl notificationHistoryService;

    private static final List<Long> members = new ArrayList<>();

    private static final List<NotificationHistory> dummy = new ArrayList<>();

    static {
        for (long i = 1; i <= 3; i++) {
            members.add(i);
        }

        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 100; i++) {
            int randomNum = ThreadLocalRandom.current().nextInt(0, members.size());
            Long memberId = members.get(randomNum);

            NotificationHistory notification = NotificationHistory.builder()
                    .id(i + "")
                    .memberId(memberId)
                    .title(String.format("this is test message (%d)", i + 1L))
                    .content(String.format("this is test message (%d)", i + 1L))
                    .type(i < 50 ? NotificationType.COMMENT_TO_POST : NotificationType.LIKE_REACTION_TO_POST)
                    .createdAt(now)
                    .lastModifiedAt(now)
                    .build();
            dummy.add(notification);
        }
    }

    @BeforeEach
    private void before() {
        this.notificationHistoryService = new NotificationHistoryServiceImpl(
                notificationHistoryRepository,
                notificationHistoryMapperMock
        );
    }

    @Test
    @DisplayName("단일 NotificationHistoryDto 조회 성공")
    void findById() {
        // Arrange
        Long memberId = 1L;
        String notificationId = "notificationId";
        LocalDateTime now = LocalDateTime.now();
        NotificationHistory history = NotificationHistory.builder()
                .id(notificationId)
                .memberId(memberId)
                .type(NotificationType.LIKE_REACTION_TO_POST)
                .title("title")
                .content("content")
                .resource(createCommentNotificationResource(memberId))
                .createdAt(now)
                .lastModifiedAt(now)
                .build();

        when(notificationHistoryRepository.findById(notificationId)).thenReturn(Mono.just(history));

        NotificationHistoryDto expected = notificationHistoryMapper.toDto(history);
        when(notificationHistoryMapperMock.toDto(any(NotificationHistory.class))).thenReturn(expected);

        // Act
        Mono<NotificationHistoryDto> result = notificationHistoryService.findById(notificationId);

        // Assert
        StepVerifier.create(result)
                .assertNext(find -> verifyEquals(expected, find))
                .verifyComplete();
    }

    @Test
    @DisplayName("단일 NotificationHistoryDto 조회 실패 (NotFoundException")
    void findById_NotFoundException() {
        // Arrange
        String notificationId = "notificationId";
        when(notificationHistoryRepository.findById(notificationId)).thenReturn(Mono.empty());

        // Act
        Mono<NotificationHistoryDto> result = notificationHistoryService.findById(notificationId);

        // Assert
        StepVerifier.create(result)
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("회원별 NotificationHistoryDto 목록 조회 성공")
    void findByMemberId() {
        // Arrange
        Long memberId = 1L;
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("createdAt")));

        Page<NotificationHistory> page = createNotificationPage(memberId, pageable);
        when(notificationHistoryRepository.findByMemberId(memberId, pageable)).thenReturn(Flux.fromIterable(page));
        for (NotificationHistory history : page.getContent()) {
            NotificationHistoryDto dto = notificationHistoryMapper.toDto(history);
            when(notificationHistoryMapperMock.toDto(history)).thenReturn(dto);
        }

        // Act
        Flux<NotificationHistoryDto> result = notificationHistoryService.findByMemberId(memberId, pageable);

        // Assert
        StepVerifier.create(result)
                .expectNextCount(page.getSize())
                .verifyComplete();
    }

    @Test
    @DisplayName("NotificationHistory 저장 성공")
    void save() {
        // Arrange
        Long memberId = 123L;
        NotificationHistoryDto dto = NotificationHistoryDto.builder()
                .memberId(memberId)
                .title("title")
                .content("content")
                .type(NotificationType.COMMENT_TO_POST)
                .resource(createCommentNotificationResource(memberId))
                .build();

        when(notificationHistoryRepository.save(any(NotificationHistory.class)))
                .thenAnswer((invocation) -> {
                    LocalDateTime createdAt = LocalDateTime.now();
                    NotificationHistory arg = invocation.getArgument(0);
                    return Mono.just(
                            NotificationHistory.builder()
                                    .id("sdkfkalsciasdjklfewr")
                                    .memberId(arg.getMemberId())
                                    .title(arg.getTitle())
                                    .content(arg.getContent())
                                    .type(arg.getType())
                                    .resource(arg.getResource())
                                    .createdAt(createdAt)
                                    .lastModifiedAt(createdAt)
                                    .build()
                    );
                });

        NotificationHistory created = NotificationHistory.builder()
                .memberId(dto.getMemberId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .type(dto.getType())
                .resource(dto.getResource())
                .build();

        NotificationHistoryDto expected = notificationHistoryMapper.toDto(created);
        when(notificationHistoryMapperMock.toDto(any(NotificationHistory.class))).thenReturn(expected);

        // Act
        Mono<NotificationHistoryDto> result = notificationHistoryService.save(dto);

        // Assert
        StepVerifier.create(result)
                .assertNext(history -> verifyEquals(expected, history))
                .verifyComplete();
    }

    private CommentNotificationResource createCommentNotificationResource(Long memberId) {
        return CommentNotificationResource.builder()
                .postResource(PostNotificationResource.builder()
                        .id(memberId)
                        .type(PostType.NORMAL)
                        .writer("postWriter")
                        .build())
                .commentId(61324L)
                .commenter("commentWriter")
                .build();
    }

    private void verifyEquals(NotificationHistoryDto expected, NotificationHistoryDto real) {
        assertEquals(expected.getNotificationHistoryId(), real.getNotificationHistoryId());
        assertEquals(expected.getMemberId(), real.getMemberId());
        assertEquals(expected.getTitle(), real.getTitle());
        assertEquals(expected.getContent(), real.getContent());
        assertEquals(expected.getType(), real.getType());
        assertEquals(expected.getResource(), real.getResource());
        assertEquals(expected.getCreatedAt(), real.getCreatedAt());
        assertEquals(expected.getLastModifiedAt(), real.getLastModifiedAt());
    }

    private Page<NotificationHistory> createNotificationPage(Long memberId, Pageable pageable) {
        List<NotificationHistory> list = dummy.stream()
                .filter(n -> n.getMemberId().equals(memberId))
                .toList();

        int start = (int) pageable.getOffset();
        int end = (int) (pageable.getOffset() + pageable.getPageSize());
        end = Math.min(end, list.size());

        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }

    private NotificationHistory findDummy(String notificationId) {
        return dummy.stream().filter(n -> n.getId().equals(notificationId)).findFirst().orElse(null);
    }

}