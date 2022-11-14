package com.honeypot.domain.notification.service;

import com.honeypot.domain.notification.entity.enums.ClientType;
import com.honeypot.domain.notification.dto.NotificationTokenDto;
import com.honeypot.domain.notification.dto.NotificationTokenUploadRequest;
import com.honeypot.domain.notification.entity.NotificationToken;
import com.honeypot.domain.notification.mapper.NotificationTokenMapper;
import com.honeypot.domain.notification.repository.NotificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class NotificationTokenManageServiceImpl implements NotificationTokenManageService {

    private final NotificationTokenRepository notificationTokenRepository;

    private final NotificationTokenMapper notificationTokenMapper;

    @Override
    @Transactional
    public Mono<NotificationTokenDto> save(Long memberId,
                                           NotificationTokenUploadRequest uploadRequest) {
        String deviceToken = uploadRequest.getDeviceToken();
        ClientType clientType = uploadRequest.getClientType();

        Optional<NotificationToken> tokenOptional
                = notificationTokenRepository.findByMemberIdAndDeviceToken(memberId, deviceToken);

        NotificationToken createdOrUpdated;
        if (tokenOptional.isEmpty()) {
            // Save if there are no existing rows
            createdOrUpdated = notificationTokenRepository.save(
                    NotificationToken.builder()
                            .deviceToken(deviceToken)
                            .clientType(clientType)
                            .memberId(memberId)
                            .build()
            );
        } else {
            createdOrUpdated = notificationTokenRepository.save(tokenOptional.get());
        }

        return Mono.just(notificationTokenMapper.toDto(createdOrUpdated));
    }

    @Override
    @Transactional
    public Mono<Void> remove(Long memberId, Long notificationTokenId) {
        return Mono.empty();
    }

}
