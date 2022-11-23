package com.honeypot.domain.notification.service;

import com.honeypot.domain.notification.dto.CommentNotificationResource;
import com.honeypot.domain.notification.dto.NotificationHistoryDto;
import com.honeypot.domain.notification.dto.PostNotificationResource;
import com.honeypot.domain.notification.entity.NotificationHistory;
import com.honeypot.domain.notification.entity.enums.NotificationType;
import com.honeypot.domain.notification.entity.enums.PostType;
import com.honeypot.domain.notification.mapper.NotificationHistoryMapper;
import com.honeypot.domain.notification.repository.NotificationHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

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

    @BeforeEach
    private void before() {
        this.notificationHistoryService = new NotificationHistoryServiceImpl(
                notificationHistoryRepository,
                notificationHistoryMapperMock
        );
    }

    @Test
    void save() {
        // Arrange
        Long memberId = 123L;

        CommentNotificationResource resource = CommentNotificationResource.builder()
                .postResource(PostNotificationResource.builder()
                        .id(memberId)
                        .type(PostType.NORMAL)
                        .writer("postWriter")
                        .build())
                .commentId(61324L)
                .commenter("commentWriter")
                .build();

        NotificationHistoryDto dto = NotificationHistoryDto.builder()
                .memberId(memberId)
                .title("title")
                .content("content")
                .type(NotificationType.COMMENT_TO_POST)
                .resource(resource)
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
                .assertNext(history -> {
                    assertEquals(expected.getNotificationHistoryId(), history.getNotificationHistoryId());
                    assertEquals(expected.getMemberId(), history.getMemberId());
                    assertEquals(expected.getTitle(), history.getTitle());
                    assertEquals(expected.getContent(), history.getContent());
                    assertEquals(expected.getType(), history.getType());
                    assertEquals(expected.getResource(), history.getResource());
                    assertEquals(expected.getCreatedAt(), history.getCreatedAt());
                    assertEquals(expected.getLastModifiedAt(), history.getLastModifiedAt());
                })
                .verifyComplete();
    }

}