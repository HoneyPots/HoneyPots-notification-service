package com.honeypot.common.config;

import com.honeypot.domain.notification.repository.NotificationHistoryRepository;
import com.honeypot.domain.notification.repository.NotificationTokenRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoAuditing
@EnableReactiveMongoRepositories(basePackageClasses = {
        NotificationTokenRepository.class,
        NotificationHistoryRepository.class
})
public class MongoConfiguration {

}
