package com.honeypot.domain.notification.mapper;

import com.honeypot.domain.notification.entity.enums.ClientType;
import com.honeypot.domain.notification.dto.NotificationTokenDto;
import com.honeypot.domain.notification.entity.NotificationToken;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NotificationTokenMapperTest {

    private final NotificationTokenMapper mapper = Mappers.getMapper(NotificationTokenMapper.class);

    @Test
    void toEntity() {
        // Arrange
        NotificationTokenDto dto = NotificationTokenDto.builder()
                .notificationTokenId("generatedTokenId")
                .memberId(1L)
                .deviceToken("notificationDeviceToken")
                .clientType(ClientType.WEB)
                .build();

        // Act
        NotificationToken entity = mapper.toEntity(dto);

        // Assert
        assertEquals(dto.getNotificationTokenId(), entity.getId());
        assertEquals(dto.getMemberId(), entity.getMemberId());
        assertEquals(dto.getDeviceToken(), entity.getDeviceToken());
        assertEquals(dto.getClientType(), entity.getClientType());
        assertEquals(dto.getCreatedAt(), entity.getCreatedAt());
        assertEquals(dto.getLastModifiedAt(), entity.getLastModifiedAt());
    }

    @Test
    void toDto() {
        // Arrange
        LocalDateTime uploadedAt = LocalDateTime.of(2022, 9, 16, 3, 0, 0);
        NotificationToken entity = NotificationToken.builder()
                .id("generatedTokenId")
                .memberId(1L)
                .deviceToken("notificationDeviceToken")
                .clientType(ClientType.AOS)
                .createdAt(uploadedAt)
                .lastModifiedAt(uploadedAt)
                .build();

        // Act
        NotificationTokenDto dto = mapper.toDto(entity);

        // Assert
        assertEquals(entity.getId(), dto.getNotificationTokenId());
        assertEquals(entity.getMemberId(), dto.getMemberId());
        assertEquals(entity.getDeviceToken(), dto.getDeviceToken());
        assertEquals(entity.getClientType(), dto.getClientType());
        assertEquals(entity.getCreatedAt(), dto.getCreatedAt());
        assertEquals(entity.getLastModifiedAt(), dto.getLastModifiedAt());
    }

}