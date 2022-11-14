package com.honeypot.domain.notification.entity;

import com.honeypot.domain.notification.dto.ClientType;
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
@Document("notificationToken")
public class NotificationToken {

    @Id
    private String id;

    private Long memberId;

    private String deviceToken;

    private ClientType clientType;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime lastModifiedAt;

}
