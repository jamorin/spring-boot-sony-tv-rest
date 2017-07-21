package com.github.jamorin.androidtv.config;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.github.jamorin.androidtv.Utils.slug;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TvConfig {

    private final ResourceLoader resourceLoader;

    /*
        The body payload that will be used to send commands to Sony Android TV.
     */
    @Bean
    public Template template(Mustache.Compiler compiler) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:/request.xml");
        String template = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
        return compiler.compile(template);
    }

    /*
        Map of TV Codes to their encoded form to be injected into the body.
     */
    @Bean
    public Map<String, String> cmdSlugs(TvProperties appProperties) {
        log.info("Mapping {} TV commands", appProperties.getCommand().size());
        Map<String, String> cmdSlugs = new HashMap<>();
        appProperties.getCommand().forEach((key, value) -> {
            cmdSlugs.put(slug(key), value);
        });
        return cmdSlugs;
    }
}
