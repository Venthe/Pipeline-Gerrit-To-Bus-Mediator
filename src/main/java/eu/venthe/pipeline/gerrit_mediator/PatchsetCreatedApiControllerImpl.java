package eu.venthe.pipeline.gerrit_mediator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.venthe.pipeline.gerrit_mediator.gerrit.api.PatchsetCreatedApi;
import eu.venthe.pipeline.gerrit_mediator.gerrit.api.RefUpdatedApi;
import eu.venthe.pipeline.gerrit_mediator.gerrit.model.PatchsetCreated;
import eu.venthe.pipeline.gerrit_mediator.gerrit.model.RefUpdated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Generated;
import javax.validation.Valid;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2022-11-25T00:13:34.969379500+01:00[Europe/Warsaw]")
@Controller
@RequestMapping("${openapi.gerrit.base-path:}")
//@Primary
@Slf4j
public class PatchsetCreatedApiControllerImpl implements PatchsetCreatedApi, RefUpdatedApi {
    @Autowired
    JenkinsConfiguration.Api api;
    @Autowired
    ObjectMapper objectMapper;

    @Override
    @RequestMapping(
            method = RequestMethod.POST,
            value = "/patchset-created",
            consumes = {"application/json"}
    )
    public Mono<ResponseEntity<String>> patchsetCreatedPost(@NotNull Mono<PatchsetCreated> patchsetCreated, ServerWebExchange exchange) {
        return patchsetCreated
                .map(a-> {
                    try {
                        return objectMapper.writeValueAsString(a);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .flatMap(a->api.jobJobNameBuildWithParametersPostWithHttpInfo(a))
                .map(a -> ResponseEntity.ok(a.getStatusCode().toString()));
    }


    @Override
    @RequestMapping(
            method = RequestMethod.POST,
            value = "/ref-updated",
            consumes = { "application/json" }
    )
    public Mono<ResponseEntity<Void>> refUpdatedPost(
            @Parameter(name = "RefUpdated", description = "") @Valid @RequestBody(required = false) Mono<RefUpdated> refUpdated,
            @Parameter(hidden = true) final ServerWebExchange exchange
    ) {
        Mono<Void> result = Mono.empty();
        exchange.getResponse().setStatusCode(HttpStatus.OK);
        return result.then(refUpdated).then(Mono.empty());

    }
}
