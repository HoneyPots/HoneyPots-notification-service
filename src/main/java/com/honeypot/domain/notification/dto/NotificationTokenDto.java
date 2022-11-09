package com.honeypot.domain.notification.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class NotificationTokenDto {

    private Long notificationTokenId;

    private Long memberId;

    private String deviceToken;

    private ClientType clientType;

    private LocalDateTime createdAt;

    private LocalDateTime lastModifiedAt;

}
