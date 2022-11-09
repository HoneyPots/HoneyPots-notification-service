package com.honeypot.common.errors.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
public class BadRequestException extends ResponseStatusException {

    public BadRequestException(String reason) {
        super(HttpStatus.BAD_REQUEST, reason);
    }

}
