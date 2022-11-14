package com.honeypot.domain.notification.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeypot.domain.notification.entity.enums.ClientType;
import com.honeypot.domain.notification.dto.NotificationTokenDto;
import com.honeypot.domain.notification.dto.NotificationTokenUploadRequest;
import com.honeypot.domain.notification.router.NotificationRouter;
import com.honeypot.domain.notification.service.NotificationTokenManageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;

@WebFluxTest(value = {
        NotificationRouter.class,
        NotificationHandler.class
})
@ExtendWith(MockitoExtension.class)
class NotificationHandlerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NotificationTokenManageService notificationTokenManageService;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToApplicationContext(context).build();
    }

    @Test
    @DisplayName("[Notification token API] 토큰 등록 성공")
    void uploadNotificationToken() throws JsonProcessingException {
        // Arrange
        Long memberId = 1L;
        NotificationTokenUploadRequest request = NotificationTokenUploadRequest.builder()
                .deviceToken("token")
                .clientType(ClientType.WEB)
                .build();

        LocalDateTime createdAt = LocalDateTime.now();
        NotificationTokenDto expected = NotificationTokenDto.builder()
                .notificationTokenId("test")
                .deviceToken(request.getDeviceToken())
                .clientType(request.getClientType())
                .memberId(memberId)
                .createdAt(createdAt)
                .lastModifiedAt(createdAt)
                .build();

        when(notificationTokenManageService.save(memberId, request)).thenReturn(Mono.just(expected));

        // Act & Assert
        webTestClient.post()
                .uri("/api/notifications/tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().location("/api/notifications/tokens/" + expected.getNotificationTokenId())
                .expectBody()
                .json(objectMapper.writeValueAsString(expected));
    }

    @Test
    @DisplayName("[Notification token API] 토큰 등록 실패 (잘못된 요청 값)")
    void uploadNotificationToken_badRequest() {
        // Arrange
        Long memberId = 1L;
        NotificationTokenUploadRequest request = NotificationTokenUploadRequest.builder()
                .deviceToken("token")
                .build();

        // Act & Assert
        webTestClient.post()
                .uri("/api/notifications/tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
    }

    @Test
    @DisplayName("[Notification token API] 토큰 삭제")
    void deleteNotificationToken() {
        // Arrange
        String tokenId = "tokenasdkfjcasdf";
        Long memberId = 1L;

        when(notificationTokenManageService.remove(memberId, tokenId)).thenReturn(Mono.empty());

        // Act & Assert
        webTestClient.delete()
                .uri("/api/notifications/tokens/" + tokenId)
                .exchange()
                .expectStatus().isNoContent();
    }

}