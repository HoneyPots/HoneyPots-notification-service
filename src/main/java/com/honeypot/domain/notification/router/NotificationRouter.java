package com.honeypot.domain.notification.router;

import com.honeypot.domain.notification.handler.NotificationHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;

@Configuration
public class NotificationRouter {

    private static final String API_PATH = "/api/notifications";

    @Bean
    public RouterFunction<ServerResponse> route(NotificationHandler notificationHandler) {
        return RouterFunctions.route()
                .path(API_PATH, b1 -> b1
                        .nest(contentType(APPLICATION_JSON), b2 -> b2
                                .POST("/tokens", notificationHandler::uploadNotificationToken)
                        )
                        .GET("/{notificationId}", notificationHandler::inquiryNotificationDetail)
                        .DELETE("/tokens/{tokenId}", notificationHandler::deleteNotificationToken)
                )
                .build();
    }

}
