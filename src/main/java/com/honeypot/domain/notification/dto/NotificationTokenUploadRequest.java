package com.honeypot.domain.notification.dto;

import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@ToString
public class NotificationTokenUploadRequest {

    @NotBlank
    private String deviceToken;

    @NotNull
    private ClientType clientType;

}
