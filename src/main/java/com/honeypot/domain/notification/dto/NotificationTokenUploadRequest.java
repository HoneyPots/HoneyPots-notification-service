package com.honeypot.domain.notification.dto;

import com.honeypot.domain.notification.entity.enums.ClientType;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class NotificationTokenUploadRequest {

    @NotBlank
    private String deviceToken;

    @NotNull
    private ClientType clientType;

}
