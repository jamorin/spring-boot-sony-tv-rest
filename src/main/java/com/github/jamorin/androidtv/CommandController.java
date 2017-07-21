package com.github.jamorin.androidtv;

import com.github.jamorin.androidtv.client.ClientResource;
import com.github.jamorin.androidtv.client.Request;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.github.jamorin.androidtv.Utils.slug;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CommandController {

    private final ClientResource resource;
    private final Map<String, String> cmdSlugs;

    @PostMapping(path = "/api/command", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void command(@Validated @RequestBody Request request) {
        String code = cmdSlugs.get(slug(request.getCommand()));
        if (code == null) {
            log.warn("Unknown command: {}", request.getCommand());
            // Always return a http status of 200, or IFTTT seems to rate limit requests on non-200's.
            return;
        }
        ResponseEntity<?> response = resource.request(code);
        log.info("{} : {} : {}", request.getCommand(), code, response.getStatusCode().getReasonPhrase());
    }

}
