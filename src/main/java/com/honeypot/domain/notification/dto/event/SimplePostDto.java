package com.honeypot.domain.notification.dto.event;

import com.honeypot.domain.notification.entity.enums.PostType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SimplePostDto {

    private Long postId;

    private PostType postType;

    private String title;

    private String content;

    private WriterDto writer;

    private LocalDateTime uploadedAt;

    private LocalDateTime lastModifiedAt;

}
