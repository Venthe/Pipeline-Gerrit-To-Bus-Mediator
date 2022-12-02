package eu.venthe.pipeline.gerrit_mediator.configuration.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.slf4j.Logger;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;

import java.time.ZonedDateTime;
import java.util.Optional;

public class LoggingWebExchange extends ServerWebExchangeDecorator {
    @Getter(onMethod_ = @Override)
    private final ServerHttpRequest request;
    @Getter(onMethod_ = @Override)
    private final ServerHttpResponse response;

    public LoggingWebExchange(ServerWebExchange delegate, ZonedDateTime startTime, Logger log, WebConfiguration webConfiguration) {
        super(delegate);
        request = new RequestLoggingDecorator(log, super.getRequest(), startTime, webConfiguration);
        response = new ResponseLoggingDecorator(log, super.getResponse(), startTime, webConfiguration);
    }
}
