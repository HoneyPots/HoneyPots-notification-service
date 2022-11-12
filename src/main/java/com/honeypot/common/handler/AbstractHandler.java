package com.honeypot.common.handler;

import com.honeypot.common.errors.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public abstract class AbstractHandler {

    private final LocalValidatorFactoryBean validator;

    public <T> Mono<ServerResponse> requireValidBody(
            Function<Mono<T>, Mono<ServerResponse>> function,
            ServerRequest request,
            Class<T> bodyClass) {
        BindingResult errors = new BeanPropertyBindingResult(request, bodyClass.getSimpleName());
        return request
                .bodyToMono(bodyClass)
                .flatMap(
                        body -> {
                            validator.validate(body, errors);
                            return !errors.hasErrors()
                                    ? function.apply(Mono.just(body))
                                    : createBadRequestResponse(errors);
                        }
                );
    }

    private Mono<ServerResponse> createBadRequestResponse(BindingResult errors) {
        throw new BadRequestException(
                errors.getFieldErrors()
                        .stream()
                        .map(e -> String.format("[%s]: %s", e.getField(), e.getDefaultMessage()))
                        .toList()
                        .toString()
        );
    }

}