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
    public List<NotificationTokenDto> findByMemberId(Long memberId) {
        return notificationTokenRepository.findByMemberId(memberId)
                .stream()
                .map(notificationTokenMapper::toDto)
                .collect(Collectors.toList());
    }

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
    public Mono<Void> remove(Long memberId, String notificationTokenId) {
        NotificationToken token = notificationTokenRepository
                .findById(notificationTokenId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("There is no token [%s]", notificationTokenId)
                ));

        if (!memberId.equals(token.getMemberId())) {
            throw new InvalidAuthorizationException();
        }

        notificationTokenRepository.delete(token);
        return Mono.empty();
    }

}
