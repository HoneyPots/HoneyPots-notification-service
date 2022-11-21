package com.honeypot.domain.notification.service;

import com.honeypot.domain.notification.dto.NotificationTokenDto;
import com.honeypot.domain.notification.dto.NotificationTokenUploadRequest;
import reactor.core.publisher.Mono;

import java.util.List;

public interface NotificationTokenManageService {

    List<NotificationTokenDto> findByMemberId(Long memberId);

    Mono<NotificationTokenDto> save(Long memberId, NotificationTokenUploadRequest request);

    Mono<Void> remove(Long memberId, String notificationTokenId);

}
