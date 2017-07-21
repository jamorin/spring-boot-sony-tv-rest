package com.github.jamorin.proxy;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.AuthorityUtils;

import static com.google.common.base.Strings.isNullOrEmpty;

@EnableZuulProxy
@Configuration
@RequiredArgsConstructor
public class ProxyConfiguration {

    private final ProxyProperties props;

    /*
        This bean will map the Github user's info to authorities.
        So that you can allow/deny access based on user info.
     */
    @Bean
    public AuthoritiesExtractor authoritiesExtractor() {
        return map -> {
            // The `login` field is your github username.
            // Map this field to a role so that we may do access control
            // If they are present in our props, they get the API role
            String role = (String) map.get("login");
            if (!isNullOrEmpty(role) && props.getOauthUsers().contains(role)) {
                return AuthorityUtils.createAuthorityList("ROLE_API");
            }
            return AuthorityUtils.createAuthorityList("ROLE_USER");
        };
    }
}
