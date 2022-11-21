package com.honeypot.domain.notification.dto.event;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentCreatedEvent {

    private SimplePostDto targetPost;

    private CommentDto createdComment;

}
