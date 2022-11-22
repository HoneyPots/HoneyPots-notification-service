package com.honeypot.domain.notification.service;

import com.honeypot.domain.notification.dto.NotificationHistoryDto;
import reactor.core.publisher.Mono;

public interface NotificationHistoryService {

    Mono<NotificationHistoryDto> save(NotificationHistoryDto history);

}
