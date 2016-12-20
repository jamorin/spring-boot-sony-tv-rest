package com.github.jamorin.bravia;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "tv", ignoreUnknownFields = false)
public class TVConfig {

    /**
     * The full endpoint URL to your Sony TV.
     * Should look something like "http://192.168.86.151/sony/IRCC"
     */
    @URL
    @NotNull
    private String url;

    /**
     * The pre-shared key setup in your Sony TV's settings.
     * Network & Accessories -> Network -> Home network -> IP control -> Pre-Shared Key
     *
     * Make sure Pre-Shared Key is turned on:
     * Network & Accessories -> Network -> Home network -> IP control -> Authentication -> Normal and Pre-Shared Key
     */
    @NotBlank
    private String preSharedKey;

    /**
     * This is a map of the command to it's base64 encoded IRCCCode code.
     */
    @NotEmpty
    private final Map<String, String> command = new LinkedHashMap<>();

}
