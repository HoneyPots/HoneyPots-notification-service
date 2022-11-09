package com.honeypot.domain.notification.handler;

import com.honeypot.common.handler.AbstractHandler;
import com.honeypot.domain.notification.dto.NotificationTokenUploadRequest;
import com.honeypot.domain.notification.service.NotificationTokenManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class NotificationHandler extends AbstractHandler {

    private final NotificationTokenManageService notificationTokenManageService;


    public Mono<ServerResponse> uploadNotificationToken(ServerRequest request) {
        return this.requireValidBody(tokenUploadRequest -> tokenUploadRequest
                .map(r -> notificationTokenManageService.save(1L, r))
                .flatMap(created -> ServerResponse
                        .created(
                                UriComponentsBuilder
                                        .fromPath(request.path() + "/{tokenId}")
                                        .buildAndExpand(created.getNotificationTokenId())
                                        .toUri()
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(created))
                ), request, NotificationTokenUploadRequest.class);
    }

}
