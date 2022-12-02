package eu.venthe.pipeline.gerrit_mediator.configuration.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class WebConfiguration {
    private final Optional<ObjectMapper> objectMapper;

    private final boolean format = true;

    public void logRequestHeader(Logger log, ZonedDateTime startTime, HttpMethod method, URI uri, HttpHeaders headers) {
        log.debug("Request({}): method={}, uri={}, headers={}", Duration.between(startTime, ZonedDateTime.now()), method,
                uri, formatHeaders(headers));
    }

    public void logRequestBody(Logger log, ZonedDateTime startTime, HttpMethod method, URI uri, HttpHeaders headers, String payload) {
        log.debug("Request({}): method={}, uri={}, headers={}, payload={}", Duration.between(startTime, ZonedDateTime.now()), method,
                uri, formatHeaders(headers), formatPayload(log, payload, headers));
    }

    public void logResponseHeader(Logger log, ZonedDateTime startTime, HttpStatusCode statusCode, HttpHeaders headers) {
        log.debug("Response({}): statusCode={}, headers={}", Duration.between(startTime, ZonedDateTime.now()), statusCode, formatHeaders(headers));
    }

    public void logResponseBody(Logger log, ZonedDateTime startTime, HttpStatusCode statusCode, HttpHeaders headers, String payload) {
        log.debug("Response({}): statusCode={}, headers={}, payload={}", Duration.between(startTime, ZonedDateTime.now()),
                statusCode, formatHeaders(headers), formatPayload(log, payload, headers));
    }

    private String formatHeaders(HttpHeaders headers) {

        return format
                ? headers.entrySet().stream()
                    .map(a -> MessageFormat.format("{0}: {1}", a.getKey(), a.getValue()))
                    .collect(Collectors.joining(System.lineSeparator()))
                : headers.toString();
    }

    public static Consumer<DataBuffer> logDataBuffer(Consumer<String> logProcessor) {
        return dataBuffer -> {
            try (var dataStream = new ByteArrayOutputStream()) {
                Channels.newChannel(dataStream).write(dataBuffer.toByteBuffer().asReadOnlyBuffer());
                String payload = dataStream.toString(StandardCharsets.UTF_8);
                logProcessor.accept(payload);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    private String formatPayload(Logger log, String plainResponse, HttpHeaders headers) {
//        if (headers.get("content-type").stream().anyMatch(a -> a.toLowerCase(Locale.ROOT).contains("json")) && objectMapper.isPresent()) {
//            ObjectMapper existingMapper = objectMapper.get();
//
//            try {
//                return existingMapper.readValue(plainResponse, JsonNode.class).toPrettyString();
//            } catch (JsonProcessingException e) {
//                log.debug("Error prettifying JSON", e);
//            }
//        }

        return plainResponse;
    }
}
