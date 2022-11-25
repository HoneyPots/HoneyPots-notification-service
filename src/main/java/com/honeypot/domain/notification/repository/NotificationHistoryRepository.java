package com.honeypot.domain.notification.repository;

import com.honeypot.domain.notification.entity.NotificationHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface NotificationHistoryRepository extends ReactiveMongoRepository<NotificationHistory, String> {

    Flux<NotificationHistory> findByMemberId(Long memberId, Pageable pageable);

}
