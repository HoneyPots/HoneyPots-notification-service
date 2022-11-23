package com.honeypot.domain.notification.repository;

import com.honeypot.domain.notification.entity.NotificationToken;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NotificationTokenRepository extends ReactiveMongoRepository<NotificationToken, String> {

    Flux<NotificationToken> findByMemberId(Long memberId);

    @Query("{memberId : ?0, deviceToken : '?1'}")
    Mono<NotificationToken> findByMemberIdAndDeviceToken(Long memberId, String deviceToken);

}
