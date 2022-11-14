package com.honeypot.common.errors.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidAuthorizationException extends ResponseStatusException {

    public InvalidAuthorizationException() {
        super(HttpStatus.FORBIDDEN, "Authorization is not sufficient.");
    }

}
