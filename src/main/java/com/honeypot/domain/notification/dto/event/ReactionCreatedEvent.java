package com.honeypot.domain.notification.dto.event;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReactionCreatedEvent {

    private SimplePostDto targetPost;

    private ReactionDto createdReaction;

}
