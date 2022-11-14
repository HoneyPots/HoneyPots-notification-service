package com.honeypot.domain.notification.service;

import com.honeypot.domain.notification.entity.enums.ClientType;
import com.honeypot.domain.notification.dto.NotificationTokenDto;
import com.honeypot.domain.notification.dto.NotificationTokenUploadRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@Validated
@RequiredArgsConstructor
public class NotificationTokenManageServiceImpl implements NotificationTokenManageService {

    @Override
    @Transactional
    public Mono<NotificationTokenDto> save(Long memberId,
                                           NotificationTokenUploadRequest uploadRequest) {

        return Mono.just(
                NotificationTokenDto.builder()
                        .notificationTokenId("test")
                        .clientType(ClientType.WEB)
                        .deviceToken("deviceToken")
                        .createdAt(LocalDateTime.now())
                        .lastModifiedAt(LocalDateTime.now())
                        .memberId(memberId)
                        .build()
        );
    }

    @Override
    @Transactional
    public Mono<Void> remove(Long memberId, Long notificationTokenId) {
        return Mono.empty();
    }

}
