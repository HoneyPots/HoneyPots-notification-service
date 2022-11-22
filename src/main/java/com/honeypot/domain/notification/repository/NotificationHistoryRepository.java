package com.honeypot.domain.notification.repository;

import com.honeypot.domain.notification.entity.NotificationHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationHistoryRepository extends MongoRepository<NotificationHistory, String> {

}
