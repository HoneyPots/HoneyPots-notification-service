package com.honeypot.domain.notification.service;

import com.honeypot.domain.notification.dto.NotificationHistoryDto;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NotificationHistoryService {

    Mono<NotificationHistoryDto> findById(String historyId);

    Flux<NotificationHistoryDto> findByMemberId(Long memberId, Pageable pageable);

    Mono<NotificationHistoryDto> save(NotificationHistoryDto history);

}
