package com.github.jamorin.androidtv.client;

import com.github.jamorin.androidtv.config.TvProperties;
import com.samskivert.mustache.Template;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static java.util.Collections.singletonMap;

/**
 * This client sends the tv remote codes to Sony Bravia API.
 */
@Service
public class ClientResource {

    private final RestTemplate restTemplate;
    private final Template template;
    private final TvProperties properties;


    public ClientResource(RestTemplateBuilder builder, TvProperties properties, Template template) {
        this.restTemplate = builder
                .setReadTimeout(10_000)
                .setConnectTimeout(5_000)
                .build();
        this.template = template;
        this.properties = properties;
    }

    public ResponseEntity<?> request(String code) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("X-Auth-PSK", properties.getPreSharedKey());
        headers.add("SOAPACTION", "\"urn:schemas-sony-com:service:IRCC:1#X_SendIRCC\"");
        headers.add("Content-Type", "text/xml; charset=UTF-8");

        String body = template.execute(singletonMap("code", code));
        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
        return restTemplate.postForEntity(properties.getUrl(), requestEntity, Void.class);
    }
}
