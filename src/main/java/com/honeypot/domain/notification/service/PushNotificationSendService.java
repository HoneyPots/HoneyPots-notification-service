package com.honeypot.domain.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.honeypot.domain.notification.dto.NotificationData;
import com.honeypot.domain.notification.dto.NotificationHistoryDto;
import com.honeypot.domain.notification.dto.NotificationResource;
import com.honeypot.domain.notification.dto.NotificationTokenDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class PushNotificationSendService implements NotificationSendService {

    private static final String MESSAGE_TITLE = "꿀단지";

    private final String fcmKeyPath;

    private final String[] fcmKeyScope;

    private final NotificationTokenManageService notificationTokenManageService;

    private final NotificationHistoryService notificationHistoryService;

    private final ObjectMapper objectMapper;

    public PushNotificationSendService(@Value("${fcm.key.path}") String fcmKeyPath,
                                       @Value("${fcm.key.scope}") String[] fcmKeyScope,
                                       NotificationTokenManageService notificationTokenManageService,
                                       NotificationHistoryService notificationHistoryService,
                                       ObjectMapper objectMapper) {
        this.fcmKeyPath = fcmKeyPath;
        this.fcmKeyScope = fcmKeyScope;
        this.notificationTokenManageService = notificationTokenManageService;
        this.notificationHistoryService = notificationHistoryService;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(
                                GoogleCredentials
                                        .fromStream(new ClassPathResource(fcmKeyPath).getInputStream())
                                        .createScoped(List.of(fcmKeyScope))
                        )
                        .build();

                FirebaseApp.initializeApp(options);
                log.info("Firebase application has been initialized");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Async
    @Override
    public <T extends NotificationResource> void send(NotificationData<T> data) {
        List<Message> messages
                = notificationTokenManageService.findByMemberId(data.getReceiverId())
                .stream()
                .map(NotificationTokenDto::getDeviceToken)
                .map(token -> message(token, data))
                .toList();

        if (!messages.isEmpty()) {
            FirebaseMessaging.getInstance().sendAllAsync(messages);
            notificationHistoryService.save(NotificationHistoryDto.toDto(data)).subscribe();
        }
    }

    private <T extends NotificationResource> Message message(String token, NotificationData<T> data) {
        try {
            return Message.builder()
                    .putData("time", LocalDateTime.now().toString())
                    .putData("data", objectMapper.writeValueAsString(data))
                    .setNotification(
                            Notification.builder()
                                    .setTitle(MESSAGE_TITLE)
                                    .setBody(data.getTitle())
                                    .build())
                    .setToken(token)
                    .build();
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
