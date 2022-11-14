package com.honeypot.domain.notification.service;

import com.honeypot.common.errors.exceptions.InvalidAuthorizationException;
import com.honeypot.domain.notification.dto.NotificationTokenDto;
import com.honeypot.domain.notification.dto.NotificationTokenUploadRequest;
import com.honeypot.domain.notification.entity.NotificationToken;
import com.honeypot.domain.notification.entity.enums.ClientType;
import com.honeypot.domain.notification.mapper.NotificationTokenMapper;
import com.honeypot.domain.notification.repository.NotificationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

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
    @DisplayName("NotificationToken 신규 등록 성공")
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
    @DisplayName("NotificationToken 이미 존재하는 경우, Version 업데이트")
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
    @DisplayName("NotificationToken 삭제 성공")
    void remove() {
        // Arrange
        Long memberId = 1242352L;
        String notificationTokenId = "tokenId";

        NotificationToken exists = create(notificationTokenId, "token", ClientType.WEB, memberId);

        when(notificationTokenRepository.findById(notificationTokenId)).thenReturn(Optional.of(exists));
        doNothing().when(notificationTokenRepository).delete(exists);

        // Act
        notificationTokenManageService.remove(memberId, notificationTokenId);

        // Assert
        verify(notificationTokenRepository, times(1)).delete(exists);
    }

    @Test
    @DisplayName("NotificationToken 삭제 실패 (InvalidAuthorization)")
    void remove_InvalidAuthorizationException() {
        // Arrange
        Long memberId = 1242352L;
        String notificationTokenId = "tokenId";

        NotificationToken exists = create(notificationTokenId, "toke2n", ClientType.IOS, memberId + 1L);

        when(notificationTokenRepository.findById(notificationTokenId)).thenReturn(Optional.of(exists));

        // Act & Assert
        assertThrows(InvalidAuthorizationException.class, () -> {
            notificationTokenManageService.remove(memberId, notificationTokenId);
        });
    }

    private NotificationToken create(String id, String token, ClientType clientType, Long memberId) {
        LocalDateTime now = LocalDateTime.now();
        return NotificationToken.builder()
                .id(id)
                .deviceToken(token)
                .clientType(clientType)
                .memberId(memberId)
                .createdAt(now)
                .lastModifiedAt(now)
                .build();
    }

}