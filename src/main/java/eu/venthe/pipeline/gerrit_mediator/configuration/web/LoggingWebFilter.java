package eu.venthe.pipeline.gerrit_mediator.configuration.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.Optional;


@Slf4j
@Component
@RequiredArgsConstructor
public class LoggingWebFilter implements WebFilter {
    private boolean formatJson = true;
    private final WebConfiguration webConfiguration;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(new LoggingWebExchange(exchange, ZonedDateTime.now(), log, webConfiguration));
    }
}
