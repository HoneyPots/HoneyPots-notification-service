package com.honeypot.domain.notification.mapper;

import com.honeypot.domain.notification.dto.*;
import com.honeypot.domain.notification.entity.NotificationHistory;
import com.honeypot.domain.notification.entity.enums.NotificationType;
import com.honeypot.domain.notification.entity.enums.PostType;
import com.honeypot.domain.notification.entity.enums.ReactionType;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NotificationHistoryMapperTest {
    private final NotificationHistoryMapper mapper = Mappers.getMapper(NotificationHistoryMapper.class);

    @Test
    void toEntity() {
        // Arrange
        CommentNotificationResource resource = CommentNotificationResource.builder()
                .postResource(PostNotificationResource.builder()
                        .id(1233L)
                        .type(PostType.NORMAL)
                        .writer("postWriter")
                        .build())
                .commentId(61324L)
                .commenter("commentWriter")
                .build();

        NotificationHistoryDto dto = NotificationHistoryDto.builder()
                .notificationHistoryId("dklscliasdf")
                .memberId(1L)
                .title("title")
                .content("content")
                .type(NotificationType.COMMENT_TO_POST)
                .resource(resource)
                .build();

        // Act
        NotificationHistory entity = mapper.toEntity(dto);

        // Assert
        assertEquals(dto.getNotificationHistoryId(), entity.getId());
        assertEquals(dto.getMemberId(), entity.getMemberId());
        assertEquals(dto.getTitle(), entity.getTitle());
        assertEquals(dto.getContent(), entity.getContent());
        assertEquals(dto.getType(), entity.getType());
        assertEquals(dto.getResource(), entity.getResource());
        assertEquals(dto.getCreatedAt(), entity.getCreatedAt());
        assertEquals(dto.getLastModifiedAt(), entity.getLastModifiedAt());
    }

    @Test
    void toDto() {
        // Arrange
        ReactionNotificationResource resource = ReactionNotificationResource.builder()
                .postResource(PostNotificationResource.builder()
                        .id(123423L)
                        .type(PostType.GROUP_BUYING)
                        .writer("postWriter")
                        .build())
                .reactionId(55325L)
                .reactionType(ReactionType.LIKE)
                .reactor("reactor")
                .build();

        LocalDateTime createdAt= LocalDateTime.now();
        NotificationHistory entity = NotificationHistory.builder()
                .id("sdciicsicds")
                .memberId(1L)
                .title("title")
                .content("content")
                .type(NotificationType.LIKE_REACTION_TO_POST)
                .resource(resource)
                .createdAt(createdAt)
                .lastModifiedAt(createdAt)
                .build();

        // Act
        NotificationHistoryDto dto = mapper.toDto(entity);

        // Assert
        assertEquals(entity.getId(), dto.getNotificationHistoryId());
        assertEquals(entity.getMemberId(), dto.getMemberId());
        assertEquals(entity.getTitle(), dto.getTitle());
        assertEquals(entity.getContent(), dto.getContent());
        assertEquals(entity.getType(), dto.getType());
        assertEquals(entity.getResource(), dto.getResource());
        assertEquals(entity.getCreatedAt(), dto.getCreatedAt());
        assertEquals(entity.getLastModifiedAt(), dto.getLastModifiedAt());
    }

}