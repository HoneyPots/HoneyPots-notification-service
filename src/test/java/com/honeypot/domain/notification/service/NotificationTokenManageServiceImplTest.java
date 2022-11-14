package com.honeypot.domain.notification.service;

import com.honeypot.domain.notification.dto.NotificationTokenDto;
import com.honeypot.domain.notification.dto.NotificationTokenUploadRequest;
import com.honeypot.domain.notification.entity.NotificationToken;
import com.honeypot.domain.notification.entity.enums.ClientType;
import com.honeypot.domain.notification.mapper.NotificationTokenMapper;
import com.honeypot.domain.notification.repository.NotificationTokenRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
class NotificationTokenManageServiceImplTest {

    private final NotificationTokenMapper notificationTokenMapper = Mappers.getMapper(NotificationTokenMapper.class);

    @Mock
    private NotificationTokenRepository notificationTokenRepository;

    @Mock
    private NotificationTokenMapper notificationTokenMapperMock;

    @InjectMocks
    private NotificationTokenManageServiceImpl notificationTokenManageService;

    @BeforeEach
    private void before() {
        this.notificationTokenManageService = new NotificationTokenManageServiceImpl(
                notificationTokenRepository,
                notificationTokenMapperMock
        );
    }

    @Test
    void save_UploadNewToken() {
        // Arrange
        Long memberId = 123L;
        NotificationTokenUploadRequest request = NotificationTokenUploadRequest.builder()
                .deviceToken("notificationDeviceToken")
                .clientType(ClientType.WEB)
                .build();

        when(notificationTokenRepository.findByMemberIdAndDeviceToken(memberId, request.getDeviceToken()))
                .thenReturn(Optional.empty());

        NotificationToken created = NotificationToken.builder()
                .deviceToken(request.getDeviceToken())
                .clientType(request.getClientType())
                .memberId(memberId)
                .build();

        when(notificationTokenRepository.save(any(NotificationToken.class)))
                .thenAnswer((invocation) -> {
                    LocalDateTime createdAt = LocalDateTime.now();
                    NotificationToken arg = invocation.getArgument(0);
                    return NotificationToken.builder()
                            .id("generatedMemberId")
                            .memberId(arg.getMemberId())
                            .deviceToken(arg.getDeviceToken())
                            .clientType(arg.getClientType())
                            .createdAt(createdAt)
                            .lastModifiedAt(createdAt)
                            .build();
                });

        NotificationTokenDto expected = notificationTokenMapper.toDto(created);
        when(notificationTokenMapperMock.toDto(any(NotificationToken.class))).thenReturn(expected);

        // Act
        Mono<NotificationTokenDto> result = notificationTokenManageService.save(memberId, request);

        // Assert
        StepVerifier.create(result)
                .assertNext(token -> {
                    assertEquals(expected.getNotificationTokenId(), token.getNotificationTokenId());
                    assertEquals(expected.getMemberId(), token.getMemberId());
                    assertEquals(expected.getDeviceToken(), token.getDeviceToken());
                    assertEquals(expected.getClientType(), token.getClientType());
                    assertEquals(expected.getCreatedAt(), token.getCreatedAt());
                    assertEquals(expected.getLastModifiedAt(), token.getLastModifiedAt());
                })
                .verifyComplete();
    }

    @Test
    void save_UpdateExistsToken() {
        // Arrange
        Long memberId = 1234124L;
        NotificationTokenUploadRequest request = NotificationTokenUploadRequest.builder()
                .deviceToken("notificationDeviceToken")
                .clientType(ClientType.WEB)
                .build();

        LocalDateTime createdAt = LocalDateTime.now();
        NotificationToken exists = NotificationToken.builder()
                .id("existedId")
                .deviceToken(request.getDeviceToken())
                .clientType(request.getClientType())
                .memberId(memberId)
                .createdAt(createdAt)
                .lastModifiedAt(createdAt)
                .build();

        when(notificationTokenRepository.findByMemberIdAndDeviceToken(memberId, request.getDeviceToken()))
                .thenReturn(Optional.of(exists));

        when(notificationTokenRepository.save(any(NotificationToken.class)))
                .thenAnswer((invocation) -> {
                    LocalDateTime modifiedAt = LocalDateTime.now();
                    NotificationToken arg = invocation.getArgument(0);
                    return NotificationToken.builder()
                            .id(arg.getId())
                            .memberId(arg.getMemberId())
                            .deviceToken(arg.getDeviceToken())
                            .clientType(arg.getClientType())
                            .createdAt(createdAt)
                            .lastModifiedAt(modifiedAt)
                            .build();
                });
        NotificationTokenDto expected = notificationTokenMapper.toDto(exists);
        when(notificationTokenMapperMock.toDto(any(NotificationToken.class))).thenReturn(expected);

        // Act
        Mono<NotificationTokenDto> result = notificationTokenManageService.save(memberId, request);

        // Assert
        StepVerifier.create(result)
                .assertNext(token -> {
                    assertEquals(expected.getNotificationTokenId(), token.getNotificationTokenId());
                    assertEquals(expected.getMemberId(), token.getMemberId());
                    assertEquals(expected.getDeviceToken(), token.getDeviceToken());
                    assertEquals(expected.getClientType(), token.getClientType());
                    assertEquals(expected.getCreatedAt(), token.getCreatedAt());
                    assertEquals(expected.getLastModifiedAt(), token.getLastModifiedAt());
                })
                .verifyComplete();
    }

    @Test
    void remove() {
    }

}