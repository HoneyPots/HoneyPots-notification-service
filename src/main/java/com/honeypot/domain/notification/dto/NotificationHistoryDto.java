package com.honeypot.domain.notification.dto;

import com.honeypot.domain.notification.entity.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationHistoryDto {

    private String notificationHistoryId;

    private Long memberId;

    private String title;

    private String content;

    private NotificationType type;

    private NotificationResource resource;

    private LocalDateTime createdAt;

    private LocalDateTime lastModifiedAt;

    public static NotificationHistoryDto toDto(NotificationData data) {
        return NotificationHistoryDto.builder()
                .memberId(data.getReceiverId())
                .title(data.getTitle())
                .content(data.getContent())
                .type(data.getType())
                .resource(data.getResource())
                .build();
    }

}
