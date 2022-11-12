package com.honeypot.domain.notification.service;

import com.honeypot.domain.notification.dto.ClientType;
import com.honeypot.domain.notification.dto.NotificationTokenDto;
import com.honeypot.domain.notification.dto.NotificationTokenUploadRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Service
@Validated
@RequiredArgsConstructor
public class NotificationTokenManageServiceImpl implements NotificationTokenManageService {

    @Override
    @Transactional
    public NotificationTokenDto save(Long memberId,
                                     NotificationTokenUploadRequest uploadRequest) {

        return NotificationTokenDto.builder()
                .notificationTokenId(1L)
                .clientType(ClientType.WEB)
                .deviceToken("deviceToken")
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .memberId(memberId)
                .build();
    }

    @Override
    @Transactional
    public void remove(Long memberId, Long notificationTokenId) {

    }

}