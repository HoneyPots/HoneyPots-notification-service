package com.honeypot.domain.notification.dto;

import com.honeypot.domain.notification.entity.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationData<T extends NotificationResource> {

    private Long receiverId;

    private NotificationType type;

    private String title;

    private String content;

    private T resource;

}
