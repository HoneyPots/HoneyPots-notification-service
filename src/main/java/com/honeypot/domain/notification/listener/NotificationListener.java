package com.honeypot.domain.notification.listener;

import com.honeypot.domain.notification.dto.CommentNotificationResource;
import com.honeypot.domain.notification.dto.NotificationData;
import com.honeypot.domain.notification.dto.PostNotificationResource;
import com.honeypot.domain.notification.dto.ReactionNotificationResource;
import com.honeypot.domain.notification.dto.event.*;
import com.honeypot.domain.notification.entity.enums.NotificationType;
import com.honeypot.domain.notification.service.NotificationSendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationListener {

    private static final String MESSAGE_COMMENT_TO_POST = "'%s'님이 새로운 댓글을 남겼습니다.";

    private static final String MESSAGE_LIKE_REACTION_TO_POST = "'%s'님이 게시글을 좋아합니다.";

    private final NotificationSendService notificationSendService;

    @RabbitListener(id = "commentEventQueue", queues = "${notification.queue.comment}")
    public void listenForCommentCreatedQueue(CommentCreatedEvent event) {
        // TODO Move to message template service
        SimplePostDto post = event.getTargetPost();
        CommentDto comment = event.getCreatedComment();
        WriterDto writer = event.getCreatedComment().getWriter();

        if (writer.getId().equals(post.getWriter().getId())) {
            return;
        }

        CommentNotificationResource resource = CommentNotificationResource.builder()
                .postResource(PostNotificationResource.builder()
                        .id(post.getPostId())
                        .type(post.getPostType())
                        .writer(post.getWriter().getNickname())
                        .build())
                .commentId(comment.getCommentId())
                .commenter(writer.getNickname())
                .build();

        notificationSendService.send(
                NotificationData.builder()
                        .receiverId(post.getWriter().getId())
                        .type(NotificationType.COMMENT_TO_POST)
                        .title(String.format(MESSAGE_COMMENT_TO_POST, writer.getNickname()))
                        .content(comment.getContent())
                        .resource(resource)
                        .build()
        );
    }

    @RabbitListener(id = "reactionEventQueue", queues = "${notification.queue.reaction}")
    public void listenForReactionCreatedQueue(ReactionCreatedEvent event) {
        // TODO Move to message template service
        SimplePostDto post = event.getTargetPost();
        ReactionDto reaction = event.getCreatedReaction();
        ReactorDto reactor = event.getCreatedReaction().getReactor();

        if (reactor.getId().equals(post.getWriter().getId()) || reaction.isAlreadyExists()) {
            return;
        }

        ReactionNotificationResource resource = ReactionNotificationResource.builder()
                .postResource(PostNotificationResource.builder()
                        .id(post.getPostId())
                        .type(post.getPostType())
                        .writer(post.getWriter().getNickname())
                        .build())
                .reactionId(reaction.getReactionId())
                .reactionType(reaction.getReactionType())
                .reactor(reactor.getNickname())
                .build();

        notificationSendService.send(
                NotificationData.builder()
                        .receiverId(post.getWriter().getId())
                        .type(NotificationType.LIKE_REACTION_TO_POST)
                        .title(String.format(MESSAGE_LIKE_REACTION_TO_POST, reactor.getNickname()))
                        .content(post.getTitle())
                        .resource(resource)
                        .build()
        );
    }

}