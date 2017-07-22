package com.github.jamorin.proxy;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.filter.ForwardedHeaderFilter;

import java.util.List;
import java.util.Map;

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
            Map<String, Object> app_metadata = (Map<String, Object>) map.get("app_metadata");
            if (!isNullOrEmpty(role) && props.getOauthUsers().contains(role)) {
                return AuthorityUtils.createAuthorityList("ROLE_API");
            } else if (app_metadata != null) {
                // Support Auth0 app_metadata where we can inject custom data via an authorities field in app_metadata.
                List<String> roles = (List<String>) app_metadata.get("authorities");
                if (roles != null) {
                    return AuthorityUtils.createAuthorityList(roles.toArray(new String[0]));
                }
            }
            return AuthorityUtils.createAuthorityList("ROLE_USER");
        };
    }

    /*
        Filter to make sure any redirected urls work behind a reverse proxy.
     */
    @Bean
    public FilterRegistrationBean forwardedHeaderFilter() {
        FilterRegistrationBean bean = new FilterRegistrationBean(new ForwardedHeaderFilter());
        bean.setOrder(-500); // Before SecurityFilterChain to fix redirecting to inaccessible internal urls after successful authentication
        return bean;
    }
}
