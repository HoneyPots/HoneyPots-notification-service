package com.honeypot.domain.notification.service;

import com.honeypot.common.errors.exceptions.InvalidAuthorizationException;
import com.honeypot.common.errors.exceptions.NotFoundException;
import com.honeypot.domain.notification.dto.NotificationTokenDto;
import com.honeypot.domain.notification.dto.NotificationTokenUploadRequest;
import com.honeypot.domain.notification.entity.NotificationToken;
import com.honeypot.domain.notification.entity.enums.ClientType;
import com.honeypot.domain.notification.mapper.NotificationTokenMapper;
import com.honeypot.domain.notification.repository.NotificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationTokenManageServiceImpl implements NotificationTokenManageService {

    private final NotificationTokenRepository notificationTokenRepository;

    private final NotificationTokenMapper notificationTokenMapper;

    @Override
    public Flux<NotificationTokenDto> findByMemberId(Long memberId) {
        return notificationTokenRepository.findByMemberId(memberId)
                .map(notificationTokenMapper::toDto);
    }

    @Override
    @Transactional
    public Mono<NotificationTokenDto> save(Long memberId,
                                           NotificationTokenUploadRequest uploadRequest) {
        String deviceToken = uploadRequest.getDeviceToken();
        ClientType clientType = uploadRequest.getClientType();

        return notificationTokenRepository.findByMemberIdAndDeviceToken(memberId, deviceToken)
                .switchIfEmpty(notificationTokenRepository.save(
                        NotificationToken.builder()
                                .deviceToken(deviceToken)
                                .clientType(clientType)
                                .memberId(memberId)
                                .build()
                ))
                .flatMap(notificationTokenRepository::save)
                .map(notificationTokenMapper::toDto);
    }

    @Override
    @Transactional
    public Mono<Void> remove(Long memberId, String notificationTokenId) {
        return notificationTokenRepository
                .findById(notificationTokenId)
                .switchIfEmpty(Mono.error(
                        new NotFoundException(String.format("There is no token [%s]", notificationTokenId))
                ))
                .flatMap(token -> {
                    if (!memberId.equals(token.getMemberId())) {
                        return Mono.error(new InvalidAuthorizationException());
                    }
                    return notificationTokenRepository.delete(token);
                });
    }

}
