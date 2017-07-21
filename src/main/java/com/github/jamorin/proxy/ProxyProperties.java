package com.github.jamorin.proxy;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "proxy", ignoreUnknownFields = false)
public class ProxyProperties {

    /**
     * White listed Github users allowed access by using browser sessions.
     */
    private Set<String> oauthUsers = new HashSet<>();

    /**
     * Basic auth users for stateless API access.
     */
    private final Map<String, String> users = new LinkedHashMap<>();
    private final Map<String, List<String>> roles = new LinkedHashMap<>();
}
