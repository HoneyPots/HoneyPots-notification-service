package com.honeypot.domain.notification.service;

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
