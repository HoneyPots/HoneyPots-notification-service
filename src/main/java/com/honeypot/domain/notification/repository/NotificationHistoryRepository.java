package com.honeypot.domain.notification.repository;

import com.honeypot.domain.notification.entity.NotificationHistory;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface NotificationHistoryRepository extends ReactiveMongoRepository<NotificationHistory, String> {

}
