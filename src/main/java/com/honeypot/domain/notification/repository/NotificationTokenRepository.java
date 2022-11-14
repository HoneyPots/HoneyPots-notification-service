package com.honeypot.domain.notification.repository;

import com.honeypot.domain.notification.entity.NotificationToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface NotificationTokenRepository extends MongoRepository<NotificationToken, String> {

    @Query("{memberId : ?0, deviceToken : '?1'}")
    Optional<NotificationToken> findByMemberIdAndDeviceToken(Long memberId, String deviceToken);

}
