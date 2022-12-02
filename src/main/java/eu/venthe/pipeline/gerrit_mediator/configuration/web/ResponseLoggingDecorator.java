package eu.venthe.pipeline.gerrit_mediator.configuration.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Optional;

import static eu.venthe.pipeline.gerrit_mediator.configuration.web.WebConfiguration.logDataBuffer;

public class ResponseLoggingDecorator extends ServerHttpResponseDecorator {

    private Logger log;
    private ZonedDateTime startTime;
    private WebConfiguration webConfiguration;

    public ResponseLoggingDecorator(Logger log, ServerHttpResponse delegate, ZonedDateTime startTime, WebConfiguration webConfiguration) {
        super(delegate);
        this.log = log;
        this.startTime = startTime;
        this.webConfiguration = webConfiguration;

        webConfiguration.logResponseHeader(log, startTime, delegate.getStatusCode(), delegate.getHeaders());
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        return super.writeWith(Flux.<DataBuffer>from(body)
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(logDataBuffer(payload -> webConfiguration.logResponseBody(log, startTime,
                        getStatusCode(), getHeaders(), payload))
                )
        );
    }
}
