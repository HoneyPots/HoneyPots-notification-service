package com.honeypot.common.config;

import com.honeypot.domain.notification.repository.NotificationTokenRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoAuditing
@EnableMongoRepositories(basePackageClasses = NotificationTokenRepository.class)
public class MongoConfiguration {

}
