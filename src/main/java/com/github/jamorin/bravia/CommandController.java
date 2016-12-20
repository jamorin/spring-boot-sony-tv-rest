package com.github.jamorin.bravia;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class CommandController {

    private final RestTemplate restTemplate;
    private final MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    private final Map<String, String> cmdSlugs = new HashMap<>();
    private final String bodyTemplate = "<?xml version=\"1.0\"?><s:Envelope " +
            "xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"" +
            "http://schemas.xmlsoap.org/soap/encoding/\"><s:Body><u:X_SendIRCC xmlns:u=\"" +
            "urn:schemas-sony-com:service:IRCC:1\"><IRCCCode>%s</IRCCCode></u:X_SendIRCC></s:Body></s:Envelope>";
    private final Config config;

    public CommandController(RestTemplateBuilder restTemplateBuilder, Config config) {
        this.restTemplate = restTemplateBuilder.build();
        this.config = config;
    }


    @PostMapping(path = "/command")
    public ResponseEntity commandEndpoint(@Validated @RequestBody Request request) {
        String sligify = SLUGIFY(request.getCommand());
        String cmd = cmdSlugs.get(sligify);
        if (cmd == null) {
            log.warn("Received unknown: {} {}", request.getCommand(), request.getDate());
            // We still give back OK. anything HTTP error code seems to cause IFTTT to rate limit requests.
            return new ResponseEntity(HttpStatus.OK);
        } else {
            log.info("{} {}", sligify, request.getDate());
        }

        String body = String.format(bodyTemplate, cmd);
        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);


        restTemplate.postForEntity(config.getUrl(), requestEntity, Void.class);

        return new ResponseEntity(HttpStatus.OK);

    }

    @PostConstruct
    public void init() {
        headers.add("X-Auth-PSK", "0000");
        headers.add("SOAPACTION", "\"urn:schemas-sony-com:service:IRCC:1#X_SendIRCC\"");
        headers.add("Content-Type", "text/xml; charset=UTF-8");

        log.info("Mapping {} tv commands", config.getCommand().size());

        for (val entry : config.getCommand().entrySet()) {
            cmdSlugs.put(SLUGIFY(entry.getKey()), entry.getValue());
        }
    }

    private static String SLUGIFY(String command) {
        return command.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

}
