package com.honeypot.domain.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeypot.domain.notification.dto.*;
import com.honeypot.domain.notification.entity.enums.NotificationType;
import com.honeypot.domain.notification.entity.enums.PostType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {PushNotificationSendService.class, ObjectMapper.class})
class PushNotificationSendServiceTest {

    @MockBean
    private NotificationTokenManageServiceImpl notificationTokenManageService;

    @MockBean
    private NotificationHistoryServiceImpl notificationHistoryService;

    @Autowired
    private PushNotificationSendService notificationSendService;

    @Test
    @Timeout(10)
    @DisplayName("푸시 알림 메시지 전송 성공")
    void send_SendSuccess() {
        // Arrange
        Long receiverId = 1L;
        NotificationType type = NotificationType.COMMENT_TO_POST;

        String token = "f2JVeYJgpSuPx_2J4854lE:APA91bGL7lNvfZMR7_TQNXgbldxoyB11FuSoODvRggXywx4OUU7Zrg-b_1q3v5UDXwTtBi02CgHc6b9ZzF93FTHNXNpn8ewdxdhy5h8iG2gLy20y5mgj-x0yEfwx8iJ-zBDfcPQFMBae";
        String token2 = "eZPPh7NxWIiWMWSSRN1YLx:APA91bEnewBtElUaFzvlg_sz5f3B58o0KdGsVf3ONJH9ZnKjOhER59o__LPrGTy0qc99PuuwamX7EbT6yV7eAdiSLjiP-7YhF2XDrvYBFRBHD27V38Iqg81AGkh0d9t41Uy6QqsgJHma";
        List<NotificationTokenDto> tokens = new ArrayList<>();
        tokens.add(NotificationTokenDto.builder().deviceToken(token).build());
        tokens.add(NotificationTokenDto.builder().deviceToken(token2).build());

        when(notificationTokenManageService.findByMemberId(receiverId)).thenReturn(Flux.fromIterable(tokens));

        CommentNotificationResource resource = CommentNotificationResource.builder()
                .postResource(PostNotificationResource.builder()
                        .id(12433L)
                        .type(PostType.NORMAL)
                        .writer("postWriter")
                        .build())
                .commentId(2222L)
                .commenter("commentWriter")
                .build();

        NotificationData<NotificationResource> data = NotificationData.builder()
                .receiverId(receiverId)
                .type(type)
                .resource(resource)
                .build();

        // Act
        notificationSendService.send(data);

        // Assert
        verify(notificationHistoryService, times(1)).save(any(NotificationHistoryDto.class));
    }

}