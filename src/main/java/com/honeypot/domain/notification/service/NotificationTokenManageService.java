package com.honeypot.domain.notification.service;

import com.honeypot.domain.notification.dto.NotificationTokenDto;
import com.honeypot.domain.notification.dto.NotificationTokenUploadRequest;
import reactor.core.publisher.Mono;

public interface NotificationTokenManageService {

    Mono<NotificationTokenDto> save(Long memberId, NotificationTokenUploadRequest request);

    Mono<Void> remove(Long memberId, String notificationTokenId);

}
