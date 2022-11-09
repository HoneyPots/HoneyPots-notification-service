package com.honeypot.domain.notification.service;

import com.honeypot.domain.notification.dto.NotificationTokenDto;
import com.honeypot.domain.notification.dto.NotificationTokenUploadRequest;

public interface NotificationTokenManageService {

    NotificationTokenDto save(Long memberId, NotificationTokenUploadRequest request);

    void remove(Long memberId, Long notificationTokenId);

}
