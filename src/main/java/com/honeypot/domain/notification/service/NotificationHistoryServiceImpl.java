package com.honeypot.domain.notification.service;

import com.honeypot.common.errors.exceptions.NotFoundException;
import com.honeypot.domain.notification.dto.NotificationHistoryDto;
import com.honeypot.domain.notification.entity.NotificationHistory;
import com.honeypot.domain.notification.mapper.NotificationHistoryMapper;
import com.honeypot.domain.notification.repository.NotificationHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class NotificationHistoryServiceImpl implements NotificationHistoryService {

    private final NotificationHistoryRepository notificationHistoryRepository;

    private final NotificationHistoryMapper notificationHistoryMapper;

    @Override
    public Mono<NotificationHistoryDto> findById(String historyId) {
        return notificationHistoryRepository.findById(historyId)
                .switchIfEmpty(Mono.error(
                        new NotFoundException("There is no notification history [" + historyId + "]")
                ))
                .map(notificationHistoryMapper::toDto);
    }

    @Override
    public Mono<NotificationHistoryDto> save(NotificationHistoryDto history) {
        return notificationHistoryRepository.save(
                NotificationHistory.builder()
                        .memberId(history.getMemberId())
                        .title(history.getTitle())
                        .content(history.getContent())
                        .type(history.getType())
                        .resource(history.getResource())
                        .build()
        ).map(notificationHistoryMapper::toDto);
    }

}
