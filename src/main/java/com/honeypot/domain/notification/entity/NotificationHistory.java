package com.honeypot.domain.notification.entity;

import com.honeypot.domain.notification.dto.NotificationResource;
import com.honeypot.domain.notification.entity.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@Document("notificationHistory")
public class NotificationHistory {

    @Id
    private String id;

    private Long memberId;

    private NotificationType type;

    private String title;

    private String content;

    private NotificationResource resource;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime lastModifiedAt;

}
