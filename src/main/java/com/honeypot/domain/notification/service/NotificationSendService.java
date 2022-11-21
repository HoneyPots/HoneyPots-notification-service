package com.honeypot.domain.notification.service;

import com.honeypot.domain.notification.dto.NotificationData;
import com.honeypot.domain.notification.dto.NotificationResource;

public interface NotificationSendService {

    <T extends NotificationResource> void send(NotificationData<T> notificationData);

}
