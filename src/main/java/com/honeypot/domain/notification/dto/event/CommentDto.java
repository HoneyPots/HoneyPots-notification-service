package com.honeypot.domain.notification.dto.event;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CommentDto {

    private long postId;

    private long commentId;

    private String content;

    private WriterDto writer;

    private LocalDateTime createdAt;

    private LocalDateTime lastModifiedAt;

}
