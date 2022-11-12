package com.honeypot.domain.notification.handler;

import com.honeypot.common.handler.AbstractHandler;
import com.honeypot.domain.notification.dto.NotificationTokenUploadRequest;
import com.honeypot.domain.notification.service.NotificationTokenManageService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Component
public class NotificationHandler extends AbstractHandler {

    private final NotificationTokenManageService notificationTokenManageService;

    public NotificationHandler(LocalValidatorFactoryBean validator,
                               NotificationTokenManageService notificationTokenManageService) {
        super(validator);
        this.notificationTokenManageService = notificationTokenManageService;
    }

    public Mono<ServerResponse> uploadNotificationToken(ServerRequest request) {
        return this.requireValidBody(tokenUploadRequest -> tokenUploadRequest
                .map(r -> notificationTokenManageService.save(1L, r))
                .flatMap(created -> created
                        .flatMap(c -> ServerResponse
                                .created(UriComponentsBuilder
                                        .fromPath(request.path() + "/{tokenId}")
                                        .buildAndExpand(c.getNotificationTokenId())
                                        .toUri()
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(BodyInserters.fromValue(c))
                        )
                ), request, NotificationTokenUploadRequest.class);
    }

    public Mono<ServerResponse> deleteNotificationToken(ServerRequest request) {
        Long tokenId = Long.parseLong(request.pathVariable("tokenId"));
        notificationTokenManageService.remove(1L, tokenId);
        return ServerResponse.noContent().build();
    }

}
