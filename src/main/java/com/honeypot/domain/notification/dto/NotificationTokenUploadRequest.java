package com.honeypot.domain.notification.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class NotificationTokenUploadRequest {

    @NotBlank
    private String deviceToken;

    @NotNull
    private ClientType clientType;

}
