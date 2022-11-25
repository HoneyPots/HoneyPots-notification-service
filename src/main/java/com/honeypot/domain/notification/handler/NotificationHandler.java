package com.honeypot.domain.notification.handler;

import com.honeypot.common.handler.AbstractHandler;
import com.honeypot.domain.notification.dto.NotificationTokenUploadRequest;
import com.honeypot.domain.notification.service.NotificationHistoryService;
import com.honeypot.domain.notification.service.NotificationTokenManageService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class NotificationHandler extends AbstractHandler {

    private final NotificationHistoryService notificationHistoryService;

    private final NotificationTokenManageService notificationTokenManageService;

    public NotificationHandler(LocalValidatorFactoryBean validator,
                               NotificationHistoryService notificationHistoryService,
                               NotificationTokenManageService notificationTokenManageService) {
        super(validator);
        this.notificationHistoryService = notificationHistoryService;
        this.notificationTokenManageService = notificationTokenManageService;
    }

    public Mono<ServerResponse> inquiryNotificationList(ServerRequest request) {
        Optional<String> pageOpt = request.queryParam("page");
        Optional<String> sizeOpt = request.queryParam("size");
        Optional<String> sortOpt = request.queryParam("sort");

        int page = 0;
        if (pageOpt.isPresent()) {
            page = Integer.parseInt(pageOpt.get());
            page = Math.max(page, 0);
        }

        int size = 20;
        if (sizeOpt.isPresent()) {
            size = Integer.parseInt(sizeOpt.get());
            size = size < 0 ? 0 : Math.min(size, 20);
        }

        Sort sort = Sort.unsorted();
        if (sortOpt.isPresent()) {
            String[] sortString = sortOpt.get().split(",");
            if (sortString.length == 2) {
                String sortField = sortString[0];
                String sortOption = sortString[1].toLowerCase();

                if (sortOption.equals("desc")) {
                    sort = Sort.by(Sort.Order.desc(sortField));
                } else if (sortOption.equals("asc")) {
                    sort = Sort.by(Sort.Order.asc(sortField));
                }
            }
        }

        Long memberId = 1L;
        return notificationHistoryService.findByMemberId(memberId, PageRequest.of(page, size, sort))
                .collectList()
                .flatMap(n -> ServerResponse.ok().bodyValue(n));
    }

    public Mono<ServerResponse> inquiryNotificationDetail(ServerRequest request) {
        String notificationId = request.pathVariable("notificationId");
        return notificationHistoryService.findById(notificationId)
                .flatMap(n -> ServerResponse.ok().bodyValue(n));
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
        String tokenId = request.pathVariable("tokenId");
        return notificationTokenManageService.remove(1L, tokenId)
                .then(ServerResponse.noContent().build());
    }

}
