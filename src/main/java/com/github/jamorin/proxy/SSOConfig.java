package com.github.jamorin.proxy;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Secures Proxied requests via Github SSO
 */
@EnableOAuth2Sso
@Configuration
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER) // Last in our list
public class SSOConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/**")
                .authorizeRequests()
                .anyRequest().hasRole("API")
            .and()
                .csrf().disable();
    }
}
