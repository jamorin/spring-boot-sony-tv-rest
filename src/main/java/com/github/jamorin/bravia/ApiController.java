package com.github.jamorin.bravia;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ApiController {

    private final ApplicationProperties config;
    private final RestTemplate clientRestTemplate;
    private final MultiValueMap<String, String> soapHeaders;
    private final Map<String, String> cmdSlugs;

    @PostMapping(path = "/command")
    public void commandEndpoint(@Validated @RequestBody Request request) {
        String command = cmdSlugs.get(Utils.slug(request.getCommand()));

        if (command != null) {
            String body = String.format(config.getRequestBodyTemplate(), command);
            HttpEntity<String> requestEntity = new HttpEntity<>(body, soapHeaders);
            ResponseEntity<Void> res = clientRestTemplate.postForEntity(config.getUrl(), requestEntity, Void.class);
            log.info("{} : {} : {}", request.getCommand(), command, res.getStatusCode().getReasonPhrase());
        } else {
            log.warn("Unknown command: {}", request.getCommand());
        }
    }

    @Data
    public static class Request {
        @NotBlank
        private String command;
    }

}
