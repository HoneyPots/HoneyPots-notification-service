package com.honeypot.domain.notification.dto.event;

import com.honeypot.domain.notification.entity.enums.ReactionTarget;
import com.honeypot.domain.notification.entity.enums.ReactionType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReactionDto {

    private Long reactionId;

    private Long targetId;

    private ReactionTarget targetType;

    private ReactionType reactionType;

    private ReactorDto reactor;

    private boolean alreadyExists;

}
