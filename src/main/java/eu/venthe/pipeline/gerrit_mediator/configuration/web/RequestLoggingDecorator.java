package eu.venthe.pipeline.gerrit_mediator.configuration.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;


public class RequestLoggingDecorator extends ServerHttpRequestDecorator {
    private final Logger log;
    private final ZonedDateTime startTime;
    private final WebConfiguration webConfiguration;

    public RequestLoggingDecorator(Logger log, ServerHttpRequest delegate, ZonedDateTime startTime, WebConfiguration webConfiguration) {
        super(delegate);
        this.log = log;
        this.startTime = startTime;
        this.webConfiguration = webConfiguration;

        webConfiguration.logRequestHeader(log, startTime, delegate.getMethod(),
                delegate.getURI(), delegate.getHeaders());
    }

    @Override
    public Flux<DataBuffer> getBody() {
        return super.getBody()
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(logDataBuffer());
    }

    private Consumer<DataBuffer> logDataBuffer() {
        return dataBuffer -> {
            try (var dataStream = new ByteArrayOutputStream()) {
                Channels.newChannel(dataStream).write(dataBuffer.toByteBuffer().asReadOnlyBuffer());
                String plainResponse = dataBuffer.toString(StandardCharsets.UTF_8);
                webConfiguration.logRequestBody(log, startTime, getDelegate().getMethod(),
                        getDelegate().getURI(), getDelegate().getHeaders(), plainResponse);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }
}
