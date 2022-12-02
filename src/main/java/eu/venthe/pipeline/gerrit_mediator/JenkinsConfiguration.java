package eu.venthe.pipeline.gerrit_mediator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import eu.venthe.pipeline.gerrit_mediator.jenkins.api.DefaultApi;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Configuration
public class JenkinsConfiguration {
    @Bean
    Api api(@Value("${jenkins.user}") String user,
            @Value("${jenkins.token}") String token,
            @Value("${jenkins.uri}") String uri,
            @Value("${jenkins.jobName}") String jobName,
            ObjectMapper objectMapper) {
        DefaultApi defaultApi = new DefaultApi();
        defaultApi.getApiClient().setUsername(user);
        defaultApi.getApiClient().setPassword(token);
        defaultApi.getApiClient().setBasePath(uri);


        return new Api(defaultApi, jobName, objectMapper);
    }

    @RequiredArgsConstructor
    public static class Api {
        private final DefaultApi defaultApi;
        private final String jobName;
        private final ObjectMapper objectMapper;


        public Mono<ResponseEntity<Void>> jobJobNameBuildWithParametersPostWithHttpInfo(String yml) {
            return jobJobNameBuildWithParametersPostWithHttpInfo(yml, file -> defaultApi.jobJobNameBuildWithParametersPostWithHttpInfo(jobName, file));
        }

        private Mono<ResponseEntity<Void>> jobJobNameBuildWithParametersPostWithHttpInfo(String content, Function<File, Mono<ResponseEntity<Void>>> function) {
            try {
                return Mono.just(File.createTempFile(Long.toString(new Date().getTime()), null, null)).publishOn(Schedulers.boundedElastic()).flatMap(tempFile -> {
                    try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                        String yml = new YAMLMapper().writeValueAsString(objectMapper.readTree(content));
                        fos.write(yml.getBytes(StandardCharsets.UTF_8));
                        return function.apply(tempFile);
                    } catch (IOException e) {
                        return Mono.error(new RuntimeException(e));
                    } finally {
                        tempFile.deleteOnExit();
                    }
                });
            } catch (IOException e) {
                return Mono.error(new RuntimeException(e));
            }
        }
    }
}
